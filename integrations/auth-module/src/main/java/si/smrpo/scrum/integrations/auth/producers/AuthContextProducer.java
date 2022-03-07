package si.smrpo.scrum.integrations.auth.producers;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.nimbusds.jwt.JWTClaimsSet;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.integrations.auth.models.errors.InvalidJwtException;
import si.smrpo.scrum.integrations.auth.services.SigningService;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.text.ParseException;
import java.util.Optional;
import java.util.Set;

import static si.smrpo.scrum.integrations.auth.JWSConstants.*;

@RequestScoped
public class AuthContextProducer {
    
    private static final Logger LOG = LogManager.getLogger(AuthContextProducer.class.getName());
    
    @Context
    private HttpServletRequest httpRequest;
    
    @Inject
    private SigningService signingService;
    
    @SuppressWarnings("unchecked")
    @Produces
    @RequestScoped
    public AuthContext produceAuthContext() {
        Optional<String> token = getHeaderValue();
        if (token.isEmpty()) {
            LOG.debug("No JWT provided!");
            throw new UnauthorizedException("error.unauthorized");
        }
        
        return token.flatMap(t -> {
                try {
                    return Optional.of(signingService.validateJwt(t));
                } catch (InvalidJwtException e) {
                    LOG.debug("Invalid jwt token!", e);
                    return Optional.empty();
                }
            })
            .flatMap(signedJwt -> {
                try {
                    JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
                    
                    AuthContext.Builder contextBuilder = AuthContext.Builder.newBuilder();
                    contextBuilder.authenticated(true);
                    contextBuilder.token(token.get());
                    contextBuilder.payload(claims.getClaims());
                    contextBuilder.id(claims.getSubject());
                    contextBuilder.email((String) claims.getClaim(EMAIL_CLAIM));
                    contextBuilder.username((String) claims.getClaim(PREFERRED_USERNAME_CLAIM));
                    contextBuilder.sysRoles((Set<String>) claims.getClaim("roles"));
                    return Optional.of(contextBuilder.build());
                } catch (ParseException e) {
                    LOG.debug("Error parsing JWT claims!", e);
                    return Optional.empty();
                }
            })
            .orElse(AuthContext.Builder.newEmptyContext());
    }
    
    private Optional<String> getHeaderValue() {
        return Optional.ofNullable(httpRequest.getHeader(HttpHeaders.AUTHORIZATION))
            .map(AuthContextProducer::trimAuthorizationHeader);
    }
    
    private static String trimAuthorizationHeader(String authorizationHeaderValue) {
        if (authorizationHeaderValue == null) {
            return null;
        }
        
        if (authorizationHeaderValue.startsWith("Bearer ")) {
            return authorizationHeaderValue.replace("Bearer ", "");
        }
        
        return authorizationHeaderValue;
    }
    
}
