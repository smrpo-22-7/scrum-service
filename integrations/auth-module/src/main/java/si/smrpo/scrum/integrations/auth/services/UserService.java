package si.smrpo.scrum.integrations.auth.services;

import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.lib.requests.UserRegisterRequest;
import si.smrpo.scrum.persistence.users.UserEntity;

import java.util.Optional;

public interface UserService {
    
    Optional<UserEntity> getUserEntityById(String userId);
    
    UserEntity checkUserCredentials(String username, String password) throws UnauthorizedException;
    
    Optional<UserEntity> getUserEntityByUsername(String username);
    
    void registerUser(UserRegisterRequest request);
    
    boolean usernameExists(String username);
}
