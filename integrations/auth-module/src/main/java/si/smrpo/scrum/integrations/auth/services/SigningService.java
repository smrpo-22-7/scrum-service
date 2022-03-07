package si.smrpo.scrum.integrations.auth.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import si.smrpo.scrum.integrations.auth.models.errors.InvalidJwtException;

import java.util.Map;

public interface SigningService {
    
    SignedJWT validateJwt(String jwt) throws InvalidJwtException;
    
    SignedJWT signJwt(String userId, Map<String, Object> claims);
    
    SignedJWT signJwt(JWTClaimsSet.Builder jwtBuilder);
    
    String serializeSignedJwt(SignedJWT signedJWT);
}
