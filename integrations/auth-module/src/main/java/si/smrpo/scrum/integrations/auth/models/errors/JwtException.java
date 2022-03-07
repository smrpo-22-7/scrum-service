package si.smrpo.scrum.integrations.auth.models.errors;

import com.mjamsek.rest.exceptions.RestException;

public class JwtException extends RestException {
    
    public JwtException() {
        super("error.jwt");
    }
}
