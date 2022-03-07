package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import org.mindrot.jbcrypt.BCrypt;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@RequestScoped
public class UserServiceImpl implements UserService {
    
    private static final Logger LOG = LogManager.getLogger(UserServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
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
    public UserEntity checkUserCredentials(String username, String password) throws UnauthorizedException {
        LOG.debug("Checking user credentials...");
        UserEntity entity = getUserEntityByUsername(username)
            .orElseThrow(() -> {
                LOG.debug("Invalid username provided!");
                return new UnauthorizedException("error.unauthorized");
            });
        LOG.trace("User successfully retrieved based on username");
        boolean validCredentials = BCrypt.checkpw(password, entity.getPassword());
        LOG.trace("Given credentials are " + (validCredentials ? "valid" : "invalid"));
        if (!validCredentials) {
            LOG.debug("Invalid user credentials provided!");
            throw new UnauthorizedException("error.unauthorized");
        }
        LOG.trace("Credentials checked");
        return entity;
    }
}
