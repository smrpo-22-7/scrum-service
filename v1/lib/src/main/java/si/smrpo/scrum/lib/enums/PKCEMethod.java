package si.smrpo.scrum.lib.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PKCEMethod {
    @JsonProperty("")
    NONE(""),
    @JsonProperty("S256")
    S256("S256"),
    @JsonProperty("plain")
    PLAIN("plain");
    
    private final String type;
    
    PKCEMethod(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public static PKCEMethod fromString(String method) throws IllegalArgumentException {
        if (method == null) {
            throw new IllegalArgumentException(String.format("Given method '%s' is not recognized or supported!", method));
        }
        for (var m : PKCEMethod.values()) {
            if (m.type.equals(method)) {
                return m;
            }
        }
        throw new IllegalArgumentException(String.format("Given method '%s' is not recognized or supported!", method));
    }
}
