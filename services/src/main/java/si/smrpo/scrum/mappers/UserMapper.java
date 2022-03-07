package si.smrpo.scrum.mappers;

import si.smrpo.scrum.lib.User;
import si.smrpo.scrum.persistence.users.UserEntity;

public class UserMapper {
    
    private UserMapper() {
    
    }
    
    public static User fromEntity(UserEntity entity) {
        User user = BaseMapper.fromEntity(entity, User.class);
        user.setUsername(entity.getUsername());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setPhoneNumber(entity.getPhoneNumber());
        user.setAvatar(entity.getAvatar());
        return user;
    }
    
}
