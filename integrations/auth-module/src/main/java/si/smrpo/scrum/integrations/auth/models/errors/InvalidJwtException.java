package si.smrpo.scrum.integrations.auth.models.errors;

import com.mjamsek.rest.exceptions.RestException;

public class InvalidJwtException extends RestException {
    
    public InvalidJwtException() {
        super("error.invalid.jwt");
    }
}
