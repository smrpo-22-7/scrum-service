package si.smrpo.scrum.integrations.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserPreferenceKey {
    @JsonProperty("auth.2fa.enabled")
    ENABLED_2FA("auth.2fa.enabled");
    
    private final String key;
    
    UserPreferenceKey(String key) {
        this.key = key;
    }
    
    public String key() {
        return this.key;
    }
    
    public static UserPreferenceKey parse(String value) {
        for(UserPreferenceKey key : values()) {
            if (key.key().equals(value)) {
                return key;
            }
        }
        throw new IllegalArgumentException("Invalid preference key!");
    }
}
