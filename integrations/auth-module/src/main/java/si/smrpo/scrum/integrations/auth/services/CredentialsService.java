package si.smrpo.scrum.integrations.auth.services;

import si.smrpo.scrum.persistence.users.UserEntity;

import java.util.Map;
import java.util.Optional;

public interface CredentialsService {
    
    void sendResetPasswordMessage(String email, String ipAddress, Map<String, String[]> params);
    
    Optional<UserEntity> validateResetPasswordChallenge(String challenge, String ipAddress);
    
}
