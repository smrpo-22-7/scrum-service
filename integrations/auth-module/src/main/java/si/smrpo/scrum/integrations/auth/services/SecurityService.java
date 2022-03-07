package si.smrpo.scrum.integrations.auth.services;

import si.smrpo.scrum.integrations.auth.models.AuthorizationGrantRequest;
import si.smrpo.scrum.integrations.auth.models.PasswordGrantRequest;
import si.smrpo.scrum.integrations.auth.models.RefreshTokenGrantRequest;
import si.smrpo.scrum.integrations.auth.models.TokenResponse;

public interface SecurityService {
    
    TokenResponse authorizationGrant(AuthorizationGrantRequest request);
    
    TokenResponse passwordGrant(PasswordGrantRequest request);
    
    TokenResponse refreshTokenGrant(RefreshTokenGrantRequest request);
    
}
