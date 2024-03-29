package si.smrpo.scrum.persistence.auth;

import si.smrpo.scrum.lib.enums.SessionStatus;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "sessions", indexes = {
    @Index(name = "IDX_SESSIONS_SEARCH", columnList = "ip_address"),
    @Index(name = "IDX_UNIQUE_IP_USER", columnList = "ip_address,user_id", unique = true)
})
@NamedQueries({
    @NamedQuery(name = SessionEntity.GET_SESSION, query = "SELECT s FROM SessionEntity s WHERE s.id = :sessionId AND s.ipAddress = :ip"),
    @NamedQuery(name = SessionEntity.GET_BY_USER_AND_IP, query = "SELECT s FROM SessionEntity s WHERE s.ipAddress = :ip AND s.user.id = :userId"),
})
public class SessionEntity extends BaseEntity {
    
    public static final String GET_SESSION = "SessionEntity.getSession";
    public static final String GET_BY_USER_AND_IP = "SessionEntity.getByUserAndIp";
    
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    
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
    
    public SessionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}
