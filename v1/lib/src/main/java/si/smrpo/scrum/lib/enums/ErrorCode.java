package si.smrpo.scrum.lib.enums;

import java.util.Optional;

public enum ErrorCode {
    LOGIN_REQUIRED("login_required", "Login required!"),
    INVALID_CREDENTIALS("invalid_credentials", "Invalid credentials!"),
    INVALID_ARGUMENTS("invalid_arguments", "Invalid arguments!"),
    PASSWORD_MISMATCH("password_mismatch", "Passwords do not match!"),
    MISSING_REQUIRED_FIELDS("missing_required_fields", "Required fields missing!"),
    INSECURE_PASSWORD("insecure_password", "Password is too short! Password must contain at least 6 characters!"),
    USERNAME_TAKEN("username_taken", "Username is taken!"),
    UNAUTHORIZED("unauthorized", "Unauthorized"),
    SERVER_ERROR("server_error", "Internal server error!");
    
    public static Optional<ErrorCode> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        for (ErrorCode error : ErrorCode.values()) {
            if (error.code.equals(code)) {
                return Optional.of(error);
            }
        }
        return Optional.empty();
    }
    
    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    private final String code;
    
    private final String description;
    
    public String code() {
        return code;
    }
    
    public String description() {
        return description;
    }
}
