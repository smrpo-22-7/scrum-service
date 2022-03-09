package si.smrpo.scrum.integrations.auth.mappers;

import si.smrpo.scrum.lib.User;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.persistence.users.UserEntity;

public class UserMapper {
    
    private UserMapper() {
    
    }
    
    public static User fromEntity(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        if (entity.getCreatedAt() != null) {
            user.setCreatedAt(entity.getCreatedAt());
        }
        if (entity.getUpdatedAt() != null) {
            user.setUpdatedAt(entity.getUpdatedAt());
        }
        user.setUsername(entity.getUsername());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setPhoneNumber(entity.getPhoneNumber());
        user.setAvatar(entity.getAvatar());
        return user;
    }
    
    public static UserProfile toProfile(UserEntity entity) {
        UserProfile user = new UserProfile();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setPhoneNumber(entity.getPhoneNumber());
        return user;
    }
    
}
