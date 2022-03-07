package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.utils.DatetimeUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import si.smrpo.scrum.integrations.auth.config.AuthConfig;
import si.smrpo.scrum.integrations.auth.models.errors.InvalidJwtException;
import si.smrpo.scrum.integrations.auth.models.errors.JwtException;
import si.smrpo.scrum.integrations.auth.registry.KeyRegistry;
import si.smrpo.scrum.integrations.auth.services.SigningService;
import si.smrpo.scrum.integrations.auth.utils.KeyUtil;
import si.smrpo.scrum.persistence.auth.SigningKeyEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@RequestScoped
public class SigningServiceImpl implements SigningService {
    
    private static final Logger LOG = LogManager.getLogger(SigningServiceImpl.class.getName());
    
    @Inject
    private AuthConfig authConfig;
    
    @Inject
    private KeyRegistry keyRegistry;
    
    private KeyFactory keyFactory;
    
    @PostConstruct
    private void init() {
        try {
            this.keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            LOG.error("RSA not supported!", e);
            throw new IllegalStateException("RSA not supported!");
        }
    }
    
    @Override
    public SignedJWT validateJwt(String jwt) throws InvalidJwtException {
        LOG.debug("Validating signed JWT");
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            LOG.trace("Token successfully parsed");
            
            RSAPublicKey publicKey = (RSAPublicKey) getPublicKey();
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            boolean valid = signedJWT.verify(verifier);
            if (!valid) {
                throw new InvalidJwtException();
            }
            LOG.trace("JWT signature is valid");
            return signedJWT;
        } catch (ParseException | JOSEException e) {
            LOG.error(e);
            throw new InvalidJwtException();
        }
    }
    
    @Override
    public String serializeSignedJwt(SignedJWT signedJWT) {
        LOG.debug("Serializing JWT...");
        return signedJWT.serialize();
    }
    
    @Override
    public SignedJWT signJwt(String userId, Map<String, Object> claims) {
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
            .subject(userId)
            .issueTime(new Date())
            .issuer(authConfig.getBaseUrl())
            .expirationTime(DatetimeUtil.getMinutesAfterNow(5));
    
        claims.forEach(claimsBuilder::claim);
        
        return signJwt(claimsBuilder.build());
    }
    
    @Override
    public SignedJWT signJwt(JWTClaimsSet.Builder jwtBuilder) {
        return signJwt(jwtBuilder.build());
    }
    
    private SignedJWT signJwt(JWTClaimsSet jwtClaimsSet) {
        LOG.debug("Signing JWT...");
        SigningKeyEntity keyEntity = keyRegistry.getKey();
        LOG.trace("Signing key loaded from key registry");
        PrivateKey privateKey = KeyUtil.loadPrivateKey(keyEntity.getPrivateKey(), keyFactory);
        JWSSigner signer = new RSASSASigner(privateKey);
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyEntity.getId()).build(),
            jwtClaimsSet
        );
        LOG.trace("JWT prepared for signing");
    
        try {
            signedJWT.sign(signer);
            LOG.trace("JWT signed");
            return signedJWT;
        } catch (JOSEException e) {
            LOG.error("Error signing JWT!", e);
            throw new JwtException();
        }
    }
    
    private PublicKey getPublicKey() {
        SigningKeyEntity keyEntity = keyRegistry.getKey();
        return KeyUtil.loadPublicKey(keyEntity.getPublicKey(), keyFactory);
    }
}
