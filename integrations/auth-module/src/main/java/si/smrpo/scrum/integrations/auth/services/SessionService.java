package si.smrpo.scrum.integrations.auth.services;

import si.smrpo.scrum.persistence.auth.SessionEntity;

import java.util.Optional;

public interface SessionService {
    
    SessionEntity startSession(String ipAddress);
    
    SessionEntity associateUserWithSession(String sessionId, String userId);
    
    SessionEntity activateSession(String sessionId);
    
    Optional<SessionEntity> getSession(String sessionId, String ipAddress);
    
    Optional<SessionEntity> getSessionById(String sessionId);
    
    void endSession(String sessionId);
    
}
