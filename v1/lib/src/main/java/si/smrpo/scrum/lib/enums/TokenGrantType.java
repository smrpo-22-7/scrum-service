package si.smrpo.scrum.lib.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum TokenGrantType {
    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token"),
    AUTHORIZATION_CODE("authorization_code");
    
    private final String type;
    
    TokenGrantType(String type) {
        this.type = type;
    }
    
    @JsonValue
    public String type() {
        return this.type;
    }
    
    public static Optional<TokenGrantType> fromString(String type) {
        if (type != null) {
            for (TokenGrantType grantType : TokenGrantType.values()) {
                if (grantType.type.equals(type)) {
                    return Optional.of(grantType);
                }
            }
        }
        return Optional.empty();
    }
    
    public static List<String> rawValues() {
        return Arrays.stream(TokenGrantType.values()).map(TokenGrantType::type).collect(Collectors.toList());
    }
}
