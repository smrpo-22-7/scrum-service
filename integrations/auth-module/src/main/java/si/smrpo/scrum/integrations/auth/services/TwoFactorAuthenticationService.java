package si.smrpo.scrum.integrations.auth.services;

import si.smrpo.scrum.persistence.auth.SessionEntity;
import si.smrpo.scrum.persistence.auth.TwoFactorCheckEntity;

import java.util.Map;
import java.util.Optional;

public interface TwoFactorAuthenticationService {
    
    Optional<TwoFactorCheckEntity> getByCode(String code);
    
    void create2FAChallenge(SessionEntity session, Map<String, Object> params);
    
    boolean verify2FAChallenge(String code, String challenge, String sessionId);
    
}
