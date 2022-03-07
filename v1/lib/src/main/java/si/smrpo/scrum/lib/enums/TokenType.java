package si.smrpo.scrum.lib.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TokenType {
    @JsonProperty("Id")
    ID("Id"),
    @JsonProperty("Bearer")
    ACCESS("Bearer"),
    @JsonProperty("Refresh")
    REFRESH("Refresh"),
    @JsonProperty("Offline")
    OFFLINE("Offline");
    
    private final String type;
    
    TokenType(String type) {
        this.type = type;
    }
    
    public String type() {
        return this.type;
    }
    
}
