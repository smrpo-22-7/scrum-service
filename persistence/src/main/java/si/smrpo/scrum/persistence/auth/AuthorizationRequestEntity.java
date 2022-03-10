package si.smrpo.scrum.persistence.auth;

import si.smrpo.scrum.lib.enums.PKCEMethod;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "authorization_requests", indexes = {
    @Index(name = "IDX_AUTHREQ_CLIENT_IP_UNIQUE", columnList = "ip_address", unique = true),
    @Index(name = "IDX_AUTHREQ_CODE_SEARCH", columnList = "code")
})
@NamedQueries({
    @NamedQuery(name = AuthorizationRequestEntity.GET_BY_IP, query = "SELECT a FROM AuthorizationRequestEntity a WHERE a.ipAddress = :ipAddress"),
    @NamedQuery(name = AuthorizationRequestEntity.GET_BY_CODE, query = "SELECT a FROM AuthorizationRequestEntity a WHERE a.code = :code AND a.codeExpiration > :nowDate")
    
})
public class AuthorizationRequestEntity extends BaseEntity {
    
    public static final String GET_BY_IP = "AuthorizationRequestEntity.getByIp";
    public static final String GET_BY_CODE = "AuthorizationRequestEntity.getByCode";
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    @Column(name = "code")
    private String code;
    
    @Column(name = "pkce_challenge", nullable = false)
    private String pkceChallenge;
    
    @Column(name = "pkce_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PKCEMethod pkceMethod;
    
    @Column(name = "code_expiration")
    @Temporal(TemporalType.TIMESTAMP)
    private Date codeExpiration;
    
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
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getPkceChallenge() {
        return pkceChallenge;
    }
    
    public void setPkceChallenge(String pkceChallenge) {
        this.pkceChallenge = pkceChallenge;
    }
    
    public PKCEMethod getPkceMethod() {
        return pkceMethod;
    }
    
    public void setPkceMethod(PKCEMethod pkceMethod) {
        this.pkceMethod = pkceMethod;
    }
    
    public Date getCodeExpiration() {
        return codeExpiration;
    }
    
    public void setCodeExpiration(Date codeExpiration) {
        this.codeExpiration = codeExpiration;
    }
}
