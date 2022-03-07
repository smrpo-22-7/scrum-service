package si.smrpo.scrum.integrations.auth.services;

import com.mjamsek.rest.exceptions.ForbiddenException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.integrations.auth.models.AuthorizationGrantRequest;
import si.smrpo.scrum.integrations.auth.models.PasswordGrantRequest;
import si.smrpo.scrum.integrations.auth.models.RefreshTokenGrantRequest;
import si.smrpo.scrum.integrations.auth.models.TokenResponse;

import javax.interceptor.InvocationContext;

public interface SecurityService {
    
    TokenResponse authorizationGrant(AuthorizationGrantRequest request);
    
    TokenResponse passwordGrant(PasswordGrantRequest request);
    
    TokenResponse refreshTokenGrant(RefreshTokenGrantRequest request);
    
    void processSecurity(InvocationContext context) throws UnauthorizedException, ForbiddenException;
    
}
