package si.smrpo.scrum.persistence.auth;

import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "login_histories", indexes = {
    @Index(name = "INDEX_LOGIN_HISTORY_USER_ID", columnList = "user_id")
})
@NamedQueries({
    @NamedQuery(name = LoginHistoryEntity.GET_USER_LAST_LOGIN, query = "SELECT h FROM LoginHistoryEntity h WHERE h.user.id = :userId ORDER BY h.createdAt DESC")
})
public class LoginHistoryEntity extends BaseEntity {
  
    public static final String GET_USER_LAST_LOGIN = "LoginHistoryEntity.getUserLastLogin";
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    public UserEntity getUser() {
        return user;
    }
    
    public void setUser(UserEntity user) {
        this.user = user;
    }
}
