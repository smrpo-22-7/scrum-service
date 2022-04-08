package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryOrder;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterOperation;
import com.kumuluz.ee.rest.enums.OrderDirection;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.*;
import com.mjamsek.rest.services.Validator;
import com.mjamsek.rest.utils.QueryUtil;
import org.mindrot.jbcrypt.BCrypt;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.config.UsersConfig;
import si.smrpo.scrum.integrations.auth.mappers.UserMapper;
import si.smrpo.scrum.integrations.auth.services.RoleService;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.User;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.requests.ChangePasswordRequest;
import si.smrpo.scrum.lib.requests.UserRegisterRequest;
import si.smrpo.scrum.persistence.auth.LoginHistoryEntity;
import si.smrpo.scrum.persistence.identifiers.UserRoleId;
import si.smrpo.scrum.persistence.users.SysRoleEntity;
import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.persistence.users.UserSysRolesEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequestScoped
public class UserServiceImpl implements UserService {
    
    private static final Logger LOG = LogManager.getLogger(UserServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private RoleService roleService;
    
    @Inject
    private Validator validator;
    
    @Inject
    private UsersConfig usersConfig;
    
    @Override
    public EntityList<User> getUserList(QueryParameters queryParameters) {
        QueryUtil.setDefaultFilterParam(new QueryFilter("status", FilterOperation.EQ, SimpleStatus.ACTIVE.name()), queryParameters);
        QueryUtil.setDefaultOrderParam(new QueryOrder("lastName", OrderDirection.ASC), queryParameters);
        
        List<User> users = JPAUtils.getEntityStream(em, UserEntity.class, queryParameters)
            .map(UserMapper::fromEntity)
            .collect(Collectors.toList());
        
        long userCount = JPAUtils.queryEntitiesCount(em, UserEntity.class, queryParameters);
        return new EntityList<>(users, userCount);
    }

    @Override
    public Set<UserEntity> getUserEntitiesByIds(List<String> userIds) {
        QueryParameters queryParameters = new QueryParameters();
        QueryUtil.overrideFilterParam(new QueryFilter("id", FilterOperation.IN, userIds), queryParameters);
        return JPAUtils.getEntityStream(em, UserEntity.class, queryParameters)
                .collect(Collectors.toSet());
    }

    @Override
    public User getUserById(String userId) {
        UserEntity entity = getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        User user = UserMapper.fromEntity(entity);
        
        Set<String> userRoles = roleService.getUserRoles(userId);
        user.setGrantedRoles(userRoles);
        
        return user;
    }
    
    @Override
    public Optional<UserEntity> getUserEntityById(String userId) {
        return Optional.ofNullable(em.find(UserEntity.class, userId));
    }
    
    @Override
    public Optional<UserEntity> getUserEntityByUsername(String username) {
        TypedQuery<UserEntity> query = em.createNamedQuery(UserEntity.GET_BY_USERNAME, UserEntity.class);
        query.setParameter("username", username);
        
        try {
            UserEntity entity = query.getSingleResult();
            return Optional.of(entity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<UserEntity> getUserEntityByEmail(String email) {
        TypedQuery<UserEntity> query = em.createNamedQuery(UserEntity.GET_BY_EMAIL, UserEntity.class);
        query.setParameter("email", email.toLowerCase());
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void registerUser(UserRegisterRequest request) {
        validator.assertNotBlank(request.getUsername());
        validator.assertNotBlank(request.getFirstName());
        validator.assertNotBlank(request.getLastName());
        validator.assertNotBlank(request.getEmail());
        validator.assertEmail(request.getEmail());
        validateCredentials(request.getPassword());
        
        if (getUserEntityByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("users.error.validation.taken-username");
        }
        
        UserEntity entity = new UserEntity();
        entity.setFirstName(request.getFirstName().trim());
        entity.setLastName(request.getLastName().trim());
        entity.setUsername(request.getUsername().trim());
        entity.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        entity.setEmail(request.getEmail().trim());
        entity.setStatus(SimpleStatus.ACTIVE);
        
        Set<SysRoleEntity> userRoles;
        if (request.getGrantedRoles() != null && request.getGrantedRoles().size() > 0) {
            userRoles = roleService.getSysRoles(request.getGrantedRoles());
        } else {
            userRoles = roleService.getSysRoleEntity(Roles.USER_ROLE)
                .map(Set::of)
                .orElse(new HashSet<>());
        }
        
        if (userRoles.size() == 0) {
            throw new ValidationException("users.error.validation.empty-roles");
        }
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            
            userRoles.forEach(userRole -> {
                UserSysRolesEntity userRoleEntity = new UserSysRolesEntity();
                UserRoleId identifier = new UserRoleId();
                identifier.setUser(entity);
                identifier.setSysRole(userRole);
                userRoleEntity.setId(identifier);
                em.persist(userRoleEntity);
            });
            
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error("Error persisting user!", e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void changePassword(String userId, ChangePasswordRequest request) {
        validateCredentials(request.getNewPassword());
        UserEntity user = checkUserPassword(userId, request.getPassword());
        
        if (user.getStatus().equals(SimpleStatus.DISABLED)) {
            throw new NotFoundException("error.not-found");
        }
        
        try {
            em.getTransaction().begin();
            user.setPassword(BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt()));
            em.merge(user);
            em.flush();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error("Error updating password!", e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void setPassword(String userId, String password) {
        validateCredentials(password);
        UserEntity user = getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
    
        if (user.getStatus().equals(SimpleStatus.DISABLED)) {
            throw new NotFoundException("error.not-found");
        }
    
        try {
            em.getTransaction().begin();
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            em.merge(user);
            em.flush();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error("Error updating password!", e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public boolean usernameExists(String username) {
        return getUserEntityByUsername(username).isPresent();
    }
    
    @Override
    public boolean isValidPassword(String password, String confirmPassword) {
        return isValidPassword(password) && isValidPassword(confirmPassword) && password.equals(confirmPassword);
    }
    
    @Override
    public boolean isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }
        if (password.length() < usersConfig.getMinPasswordLength()) {
            return false;
        }
        if (password.length() >= usersConfig.getMaxPasswordLength()) {
            return false;
        }
        return true;
    }
    
    @Override
    public User updateUser(String userId, User user) {
        UserEntity entity = getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            if (getUserEntityByUsername(user.getUsername()).isPresent()) {
                throw new ConflictException("users.error.validation.taken-username");
            }
        }
        
        try {
            em.getTransaction().begin();
            
            setIfNotNull(user.getUsername(), entity::setUsername);
            setIfNotNull(user.getEmail(), entity::setEmail);
            setIfNotNull(user.getFirstName(), entity::setFirstName);
            setIfNotNull(user.getLastName(), entity::setLastName);
            setIfNotNull(user.getPhoneNumber(), entity::setPhoneNumber);
            setIfNotNull(user.getAvatar(), entity::setAvatar);
            
            em.getTransaction().commit();
            return UserMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public UserProfile getUserProfile(String userId) {
        return getUserEntityById(userId)
            .flatMap(entity -> {
                if (entity.getStatus().equals(SimpleStatus.DISABLED)) {
                    return Optional.empty();
                }
                return Optional.of(entity);
            })
            .map(UserMapper::toProfile)
            .orElseThrow(() -> new UnauthorizedException("error.unauthorized"));
    }
    
    @Override
    public void updateUserProfile(String userId, UserProfile userProfile) {
        UserEntity user = getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (userProfile.getUsername() != null && !userProfile.getUsername().isBlank()) {
            if (!userProfile.getUsername().equals(user.getUsername())) {
                if (getUserEntityByUsername(userProfile.getUsername()).isPresent()) {
                    throw new ConflictException("users.error.validation.taken-username");
                }
            }
        }
        if (userProfile.getEmail() != null && !userProfile.getEmail().isBlank()) {
            if (!userProfile.getEmail().equals(user.getEmail())) {
                validator.assertEmail(userProfile.getEmail());
            }
        }
        
        try {
            em.getTransaction().begin();
            
            setIfNotNull(userProfile.getUsername(), user::setUsername);
            setIfNotNull(userProfile.getFirstName(), user::setFirstName);
            setIfNotNull(userProfile.getLastName(), user::setLastName);
            setIfNotNull(userProfile.getEmail(), user::setEmail);
            setIfNotNull(userProfile.getPhoneNumber(), user::setPhoneNumber);
            
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void changeUserStatus(String userId, SimpleStatus status) {
        UserEntity entity = getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        try {
            em.getTransaction().begin();
            entity.setStatus(status);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<LoginHistoryEntity> getUsersLastLogin(String userId) {
        try {
            TypedQuery<LoginHistoryEntity> query = em.createNamedQuery(LoginHistoryEntity.GET_USER_LAST_LOGIN, LoginHistoryEntity.class);
            query.setParameter("userId", userId);
            query.setMaxResults(1);
            query.setFirstResult(1);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void saveLoginEvent(String userId) {
        UserEntity user = getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        try {
            em.getTransaction().begin();
            LoginHistoryEntity entity = new LoginHistoryEntity();
            entity.setUser(user);
            em.persist(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public UserEntity checkUserCredentials(String username, String password) throws UnauthorizedException {
        LOG.debug("Checking user credentials...");
        UserEntity entity = getUserEntityByUsername(username)
            .orElseThrow(() -> {
                LOG.debug("Invalid username provided!");
                return new UnauthorizedException("error.unauthorized");
            });
        
        if (entity.getStatus().equals(SimpleStatus.DISABLED)) {
            LOG.debug("User is disabled!");
            throw new UnauthorizedException("error.unauthorized");
        }
        LOG.trace("User successfully retrieved based on username");
        return checkUserCredentials(entity, password);
    }
    
    private UserEntity checkUserPassword(String userId, String password) throws UnauthorizedException {
        UserEntity user = getUserEntityById(userId)
            .orElseThrow(() -> new UnauthorizedException("error.unauthorized"));
        return checkUserCredentials(user, password);
    }
    
    private UserEntity checkUserCredentials(UserEntity user, String password) throws UnauthorizedException {
        boolean validCredentials = BCrypt.checkpw(password, user.getPassword());
        LOG.trace("Given credentials are " + (validCredentials ? "valid" : "invalid"));
        if (!validCredentials) {
            LOG.debug("Invalid user credentials provided!");
            throw new UnauthorizedException("error.unauthorized");
        }
        LOG.trace("Credentials checked");
        return user;
    }
    
    private void validateCredentials(String password) throws ValidationException {
        validator.assertNotBlank(password);
        if (password.length() < usersConfig.getMinPasswordLength()) {
            throw new ValidationException("users.validation.error.password.short").isValidationError();
        }
        
        if (password.length() >= usersConfig.getMaxPasswordLength()) {
            throw new ValidationException("users.validation.error.password.long").isValidationError();
        }
    }
    
    private <T> void setIfNotNull(T value, Consumer<T> setter, boolean allowBlank) {
        if (value != null) {
            if (value instanceof String && !allowBlank) {
                String stringValue = (String) value;
                if (!stringValue.trim().isBlank()) {
                    setter.accept(value);
                }
            } else {
                setter.accept(value);
            }
        }
    }
    
    private <T> void setIfNotNull(T value, Consumer<T> setter) {
        setIfNotNull(value, setter, false);
    }
}
