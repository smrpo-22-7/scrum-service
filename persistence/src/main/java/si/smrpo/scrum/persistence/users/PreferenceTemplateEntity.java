package si.smrpo.scrum.persistence.users;

import si.smrpo.scrum.lib.enums.DataType;
import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "preference_templates", indexes = {
    @Index(name = "UNIQUE_INDEX_PREFS_TEMPLATE_KEY", columnList = "preference_key", unique = true)
})
@NamedQueries({
    @NamedQuery(name = PreferenceTemplateEntity.GET_BY_KEY, query = "SELECT p FROM PreferenceTemplateEntity p WHERE p.preferenceKey = :key")
})
public class PreferenceTemplateEntity extends BaseEntity {
    
    public static final String GET_BY_KEY = "PreferenceTemplateEntity.getByKey";
    
    @Column(name = "preference_key", nullable = false)
    private String preferenceKey;
    
    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DataType dataType;
    
    @Column(name = "default_value")
    private String defaultValue;
    
    public String getPreferenceKey() {
        return preferenceKey;
    }
    
    public void setPreferenceKey(String preferenceKey) {
        this.preferenceKey = preferenceKey;
    }
    
    public DataType getDataType() {
        return dataType;
    }
    
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
