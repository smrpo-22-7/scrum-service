package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.exceptions.ValidationException;
import com.mjamsek.rest.services.Validator;
import org.mindrot.jbcrypt.BCrypt;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.config.UsersConfig;
import si.smrpo.scrum.integrations.auth.mappers.UserMapper;
import si.smrpo.scrum.integrations.auth.services.RoleService;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.requests.ChangePasswordRequest;
import si.smrpo.scrum.lib.requests.UserRegisterRequest;
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
import java.util.Optional;
import java.util.Set;

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
    public void registerUser(UserRegisterRequest request) {
        validator.assertNotBlank(request.getUsername());
        validator.assertNotBlank(request.getFirstName());
        validator.assertNotBlank(request.getLastName());
        validator.assertNotBlank(request.getEmail());
        validator.assertEmail(request.getEmail());
        validateCredentials(request.getPassword());
        
        UserEntity entity = new UserEntity();
        entity.setFirstName(request.getFirstName().trim());
        entity.setLastName(request.getLastName().trim());
        entity.setUsername(request.getUsername().trim());
        entity.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        entity.setEmail(request.getEmail().trim());
    
        Set<SysRoleEntity> userRoles;
        if (request.getGrantedRoles() != null && request.getGrantedRoles().size() > 0) {
            userRoles = roleService.getSysRoles(request.getGrantedRoles());
        } else {
            userRoles = roleService.getSysRoleEntity(Roles.USER_ROLE)
                .map(Set::of)
                .orElse(new HashSet<>());
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
    public boolean usernameExists(String username) {
        return getUserEntityByUsername(username).isPresent();
    }
    
    @Override
    public UserProfile getUserProfile(String userId) {
        return getUserEntityById(userId)
            .map(UserMapper::toProfile)
            .orElseThrow(() -> new UnauthorizedException("error.unauthorized"));
    }
    
    @Override
    public UserEntity checkUserCredentials(String username, String password) throws UnauthorizedException {
        LOG.debug("Checking user credentials...");
        UserEntity entity = getUserEntityByUsername(username)
            .orElseThrow(() -> {
                LOG.debug("Invalid username provided!");
                return new UnauthorizedException("error.unauthorized");
            });
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
}
