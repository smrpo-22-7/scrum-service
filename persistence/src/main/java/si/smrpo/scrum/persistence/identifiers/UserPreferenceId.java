package si.smrpo.scrum.persistence.identifiers;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserPreferenceId implements Serializable {
    
    @Column(name = "preference_key")
    private String preferenceKey;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    public String getPreferenceKey() {
        return preferenceKey;
    }
    
    public void setPreferenceKey(String preferenceKey) {
        this.preferenceKey = preferenceKey;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
