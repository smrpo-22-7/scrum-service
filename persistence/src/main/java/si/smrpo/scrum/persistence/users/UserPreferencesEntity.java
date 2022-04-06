package si.smrpo.scrum.persistence.users;

import si.smrpo.scrum.lib.enums.DataType;
import si.smrpo.scrum.persistence.identifiers.UserPreferenceId;

import javax.persistence.*;

@Entity
@Table(name = "user_preferences")
@NamedQueries({
    @NamedQuery(name = UserPreferencesEntity.GET_BY_KEY_AND_USER, query = "SELECT up FROM UserPreferencesEntity up WHERE up.id.preferenceKey = :key AND up.id.userId = :userId"),
    @NamedQuery(name = UserPreferencesEntity.GET_BY_KEYS_AND_USER, query = "SELECT up FROM UserPreferencesEntity up WHERE up.id.preferenceKey IN :keys AND up.id.userId = :userId"),
    @NamedQuery(name = UserPreferencesEntity.GET_BY_USER, query = "SELECT up FROM UserPreferencesEntity up WHERE up.id.userId = :userId")
})
public class UserPreferencesEntity {
    
    public static final String GET_BY_KEY_AND_USER = "UserPreferencesEntity.getByKeyAndUser";
    public static final String GET_BY_USER = "UserPreferencesEntity.getByUser";
    public static final String GET_BY_KEYS_AND_USER = "UserPreferencesEntity.getByKeysAndUser";
    
    @EmbeddedId
    private UserPreferenceId id;
    
    @Column(name = "preference_value")
    private String preferenceValue;
    
    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DataType dataType;
    
    public String getPreferenceKey() {
        return this.id.getPreferenceKey();
    }
    
    public String getUserId() {
        return this.id.getUserId();
    }
    
    public UserPreferenceId getId() {
        return id;
    }
    
    public void setId(UserPreferenceId id) {
        this.id = id;
    }
    
    public String getPreferenceValue() {
        return preferenceValue;
    }
    
    public void setPreferenceValue(String preferenceValue) {
        this.preferenceValue = preferenceValue;
    }
    
    public DataType getDataType() {
        return dataType;
    }
    
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
