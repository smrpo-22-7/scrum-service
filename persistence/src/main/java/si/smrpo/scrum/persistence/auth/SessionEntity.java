package si.smrpo.scrum.persistence.auth;

import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "sessions", indexes = {
    @Index(name = "IDX_SESSIONS_SEARCH", columnList = "ip_address")
})
@NamedQueries({
    @NamedQuery(name = SessionEntity.GET_SESSION, query = "SELECT s FROM SessionEntity s WHERE s.id = :sessionId AND s.ipAddress = :ip")
})
public class SessionEntity extends BaseEntity {
    
    public static final String GET_SESSION = "SessionEntity.getSession";
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public UserEntity getUser() {
        return user;
    }
    
    public void setUser(UserEntity user) {
        this.user = user;
    }
}
