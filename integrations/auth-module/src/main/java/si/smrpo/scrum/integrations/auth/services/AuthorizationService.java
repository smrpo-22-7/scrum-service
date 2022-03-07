package si.smrpo.scrum.integrations.auth.services;

import si.smrpo.scrum.lib.enums.PKCEMethod;
import si.smrpo.scrum.persistence.auth.AuthorizationRequestEntity;

import java.util.Optional;

public interface AuthorizationService {
    
    AuthorizationRequestEntity initializeRequest(String userIp, String pkceChallenge, PKCEMethod pkceMethod);
    
    AuthorizationRequestEntity createAuthorizationCode(String requestId, String userId);
    
    AuthorizationRequestEntity initializeSessionRequest(String sessionId, String ipAddress, String pkceChallenge, PKCEMethod pkceMethod);
    
    AuthorizationRequestEntity recordPKCEChallenge(String requestId, String pkceChallenge, PKCEMethod pkceMethod);
    
    void removeAuthorizationRequest(String requestId);
    
    Optional<AuthorizationRequestEntity> getRequestByCode(String code);
    
    Optional<AuthorizationRequestEntity> getRequestEntityByIp(String ipAddress);
    
    Optional<AuthorizationRequestEntity> getRequestEntityById(String requestId);
    
}
