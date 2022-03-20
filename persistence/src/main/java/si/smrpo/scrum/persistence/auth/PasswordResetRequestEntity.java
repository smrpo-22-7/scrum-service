package si.smrpo.scrum.persistence.auth;

import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "password_reset_requests", indexes = {
    @Index(name = "INDEX_PASS_RESET_REQ_USER_ID", columnList = "user_id")
})
@NamedQueries({
    @NamedQuery(name = PasswordResetRequestEntity.GET_BY_CHALLENGE, query = "SELECT r FROM PasswordResetRequestEntity r WHERE r.challenge = :challenge")
})
public class PasswordResetRequestEntity extends BaseEntity {
    
    public static final String GET_BY_CHALLENGE = "PasswordResetRequestEntity.getByChallenge";
    public static final String GET_BY_USER_ID_AND_IP = "PasswordResetRequestEntity.getByUserIdAndIp";
    
    @Column(name = "challenge")
    private String challenge;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_date")
    private Date expirationDate;
    
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
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public String getChallenge() {
        return challenge;
    }
    
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }
}
