package si.smrpo.scrum.persistence.auth;

import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "two_factor_checks", indexes = {
    @Index(name = "TWO_FACTOR_CODE", columnList = "verification_code")
})
@NamedQueries({
    @NamedQuery(name = TwoFactorCheckEntity.GET_BY_CODE, query = "SELECT c FROM TwoFactorCheckEntity c WHERE c.verificationCode = :code")
})
public class TwoFactorCheckEntity extends BaseEntity {
    
    public static final String GET_BY_CODE = "TwoFactorCheckEntity.getByCode";
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private SessionEntity session;
    
    @Column(name = "verification_code", nullable = false)
    private String verificationCode;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;
    
    public SessionEntity getSession() {
        return session;
    }
    
    public void setSession(SessionEntity session) {
        this.session = session;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
