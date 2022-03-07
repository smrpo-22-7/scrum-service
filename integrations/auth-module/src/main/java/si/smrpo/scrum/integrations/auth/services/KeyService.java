package si.smrpo.scrum.integrations.auth.services;

import si.smrpo.scrum.persistence.auth.SigningKeyEntity;

public interface KeyService {
    
    void initializeKeys();
    
    SigningKeyEntity getSigningKey();
    
    void rotateKeys();
    
    void loadKeysToRegistry();
    
}
