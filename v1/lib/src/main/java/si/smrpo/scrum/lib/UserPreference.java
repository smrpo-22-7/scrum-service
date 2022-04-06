package si.smrpo.scrum.lib;

import si.smrpo.scrum.lib.enums.DataType;

public class UserPreference {
    
    private String key;
    
    private String userId;
    
    private String value;
    
    private DataType dataType;
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public DataType getDataType() {
        return dataType;
    }
    
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
