package si.smrpo.scrum.integrations.preferences;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import si.smrpo.scrum.persistence.users.UserPreferencesEntity;

import java.util.Map;
import java.util.Optional;

public interface UserPreferences {
    
    Map<UserPreferenceKey, UserPreferencesEntity> getUserPreferences(String userId);
    
    Optional<UserPreferencesEntity> getUserPreference(UserPreferenceKey key, String userId);
    Optional<String> getString(UserPreferenceKey key, String userId);
    Optional<Integer> getLong(UserPreferenceKey key, String userId);
    Optional<Boolean> getBoolean(UserPreferenceKey key, String userId);
    Optional<Double> getDecimal(UserPreferenceKey key, String userId);
    Optional<JsonNode> getJSON(UserPreferenceKey key, String userId);
    
    void updateUserPreference(UserPreferenceKey key, String value, String userId);
    void updateUserPreference(UserPreferenceKey key, Long value, String userId);
    void updateUserPreference(UserPreferenceKey key, Boolean value, String userId);
    void updateUserPreference(UserPreferenceKey key, Double value, String userId);
    void updateUserPreference(UserPreferenceKey key, ObjectNode value, String userId);
}
