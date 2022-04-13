package si.smrpo.scrum.persistence.identifiers;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserPreferenceId implements Serializable {
    
    @Column(name = "preference_key", nullable = false)
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreferenceId that = (UserPreferenceId) o;
        return preferenceKey.equals(that.preferenceKey) && userId.equals(that.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(preferenceKey, userId);
    }
}
