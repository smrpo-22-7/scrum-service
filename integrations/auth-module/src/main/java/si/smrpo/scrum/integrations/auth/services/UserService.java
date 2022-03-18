package si.smrpo.scrum.integrations.auth.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.lib.User;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.requests.ChangePasswordRequest;
import si.smrpo.scrum.lib.requests.UserRegisterRequest;
import si.smrpo.scrum.persistence.users.UserEntity;

import java.util.Optional;

public interface UserService {
    
    EntityList<User> getUserList(QueryParameters queryParameters);
    
    User getUserById(String userId);
    
    Optional<UserEntity> getUserEntityById(String userId);
    
    UserEntity checkUserCredentials(String username, String password) throws UnauthorizedException;
    
    Optional<UserEntity> getUserEntityByUsername(String username);
    
    void registerUser(UserRegisterRequest request);
    
    void changePassword(String userId, ChangePasswordRequest request);
    
    boolean usernameExists(String username);
    
    User updateUser(String userId, User user);
    
    UserProfile getUserProfile(String userId);
    
    void updateUserProfile(String userId, UserProfile userProfile);
    
    void changeUserStatus(String userId, SimpleStatus status);
    
}
