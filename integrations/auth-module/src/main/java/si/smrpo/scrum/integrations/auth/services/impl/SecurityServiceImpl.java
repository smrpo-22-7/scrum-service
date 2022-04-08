package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.ForbiddenException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.utils.DatetimeUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import si.smrpo.scrum.integrations.auth.JWSConstants;
import si.smrpo.scrum.integrations.auth.config.AuthConfig;
import si.smrpo.scrum.integrations.auth.models.*;
import si.smrpo.scrum.integrations.auth.models.annotations.PublicResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.integrations.auth.models.errors.InvalidJwtException;
import si.smrpo.scrum.integrations.auth.services.*;
import si.smrpo.scrum.lib.enums.PKCEMethod;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.TokenType;
import si.smrpo.scrum.persistence.auth.AuthorizationRequestEntity;
import si.smrpo.scrum.persistence.auth.LoginHistoryEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static si.smrpo.scrum.integrations.auth.JWSConstants.*;

@RequestScoped
public class SecurityServiceImpl implements SecurityService {
    
    private static final Logger LOG = LogManager.getLogger(SecurityServiceImpl.class.getName());
    
    @Inject
    private SigningService signingService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private RoleService roleService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Inject
    private AuthConfig authConfig;
    
    @Inject
    private AuthContext authContext;
    
    @Override
    public TokenResponse authorizationGrant(AuthorizationGrantRequest req) {
        LOG.debug("Starting authorization code grant flow...");
        AuthorizationRequestEntity request = authorizationService.getRequestByCode(req.getCode())
            .orElseThrow(() -> {
                LOG.debug("Invalid authorization code provided!");
                return new UnauthorizedException("error.unauthorized");
            });
        LOG.trace("Recognized existing request with valid authorization code");
        authorizationService.removeAuthorizationRequest(request.getId());
        LOG.trace("Removed authorization request");
        
        UserEntity user = request.getUser();
        Set<String> roles = roleService.getUserRoles(user.getId());
        
        verifyPKCEChallenge(request.getPkceChallenge(), req.getCodeVerifier(), PKCEMethod.S256);
        
        return createToken(user, roles);
    }
    
    @Override
    public TokenResponse passwordGrant(PasswordGrantRequest request) {
        LOG.debug("Starting direct access password grant flow...");
        UserEntity user = userService.checkUserCredentials(request.getUsername(), request.getPassword());
        LOG.debug("User credentials OK");
        Set<String> userRoles = roleService.getUserRoles(user.getId());
        return createToken(user, userRoles);
    }
    
    @Override
    public TokenResponse refreshTokenGrant(RefreshTokenGrantRequest request) {
        LOG.debug("Starting refresh token grant flow...");
        try {
            SignedJWT jwt = signingService.validateJwt(request.getRefreshToken());
            LOG.debug("Refresh token is valid");
            JWTClaimsSet claims = getClaims(jwt);
            if (!claims.getClaim(JWSConstants.TYPE_CLAIM).equals(TokenType.REFRESH.type())) {
                LOG.debug("Invalid token type. Token must be of type refresh.");
                throw new UnauthorizedException("error.unauthorized");
            }
            
            UserEntity user = userService.getUserEntityById(claims.getSubject())
                .orElseThrow(() -> {
                    LOG.debug("User matching JWT subject cannot be found");
                    return new UnauthorizedException("error.unauthorized");
                });
            if (user.getStatus().equals(SimpleStatus.DISABLED)) {
                LOG.debug("Refused issuing refresh token to disabled user.");
                throw new UnauthorizedException("error.unauthorized");
            }
            
            Set<String> userRoles = roleService.getUserRoles(user.getId());
            
            return createToken(user, userRoles);
        } catch (InvalidJwtException e) {
            LOG.debug("Invalid JWT token", e);
            throw new UnauthorizedException("error.unauthorized");
        }
    }
    
    @Override
    public void processSecurity(InvocationContext context) throws UnauthorizedException, ForbiddenException {
        SysRolesRequired sysRoles = getSysRolesRequiredAnnotation(context.getMethod());
        if (sysRoles != null) {
            if (this.isNotPublic(context.getMethod())) {
                this.validateSysRoles(sysRoles);
            }
        } else {
            if (this.isNotPublic(context.getMethod())) {
                this.validateAuthenticated();
            }
        }
    }
    
    private void verifyPKCEChallenge(String codeChallenge, String codeVerifier, PKCEMethod method) throws UnauthorizedException {
        LOG.debug("Verifying PKCE challenge...");
        if (method.equals(PKCEMethod.PLAIN)) {
            LOG.trace("Using plain PKCE method");
            if (!codeChallenge.equals(codeVerifier)) {
                LOG.debug("Invalid PKCE challenge!");
                throw new UnauthorizedException("error.unauthorized");
            }
            LOG.debug("PKCE challenge OK");
        }
        
        if (method.equals(PKCEMethod.S256)) {
            LOG.trace("Using S256 PKCE method");
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
                String base64UrlEncoded = Base64.getUrlEncoder().encodeToString(hash)
                    .replaceAll("=", "")
                    .replaceAll("/", "_")
                    .replaceAll("\\+", "-");
                if (!base64UrlEncoded.equals(codeChallenge)) {
                    LOG.debug("Invalid PKCE challenge!");
                    throw new UnauthorizedException("error.unauthorized");
                }
                LOG.debug("PKCE challenge OK");
            } catch (NoSuchAlgorithmException e) {
                LOG.error("Platform doesn't support SHA-256!", e);
                throw new RestException("error.server");
            }
        }
    }
    
    private TokenResponse createToken(UserEntity user, Set<String> roles) {
        LOG.debug("Creating tokens...");
        
        Date lastLogin = userService.getUsersLastLogin(user.getId())
            .map(LoginHistoryEntity::getCreatedAt)
            .orElse(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        
        TokenResponse response = new TokenResponse();
        
        // Common claims
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLES_CLAIM, roles);
        claims.put(GIVEN_NAME_CLAIM, user.getFirstName());
        claims.put(FAMILY_NAME_CLAIM, user.getLastName());
        claims.put(NAME_CLAIM, user.getFirstName() + " " + user.getLastName());
        claims.put(EMAIL_CLAIM, user.getEmail());
        claims.put(PREFERRED_USERNAME_CLAIM, user.getUsername());
        claims.put(LAST_LOGIN_CLAIM, dateFormat.format(lastLogin));
      
        JWTClaimsSet.Builder commonBuilder = new JWTClaimsSet.Builder()
            .subject(user.getId())
            .issueTime(new Date())
            .issuer(authConfig.getBaseUrl());
        
        claims.forEach(commonBuilder::claim);
        
        JWTClaimsSet.Builder accessTokenBuilder = commonBuilder
            .claim(TYPE_CLAIM, TokenType.ACCESS.type())
            .expirationTime(DatetimeUtil.getMinutesAfterNow(5));
        
        String accessToken = signingService.serializeSignedJwt(signingService.signJwt(accessTokenBuilder));
        response.setAccessToken(accessToken);
        LOG.trace("Access token created");
        
        JWTClaimsSet.Builder refreshTokenBuilder = commonBuilder
            .claim(TYPE_CLAIM, TokenType.REFRESH.type())
            .expirationTime(DatetimeUtil.getMinutesAfterNow(60));
        
        String refreshToken = signingService.serializeSignedJwt(signingService.signJwt(refreshTokenBuilder));
        response.setRefreshToken(refreshToken);
        LOG.trace("Refresh token created");
        
        JWTClaimsSet.Builder idTokenBuilder = commonBuilder
            .claim(TYPE_CLAIM, TokenType.ID.type())
            .expirationTime(DatetimeUtil.getMinutesAfterNow(1));
        
        String idToken = signingService.serializeSignedJwt(signingService.signJwt(idTokenBuilder));
        response.setIdToken(idToken);
        LOG.trace("Id token created");
        
        response.setExpiresIn(5 * 60);
        LOG.debug("Tokens created!");
        return response;
    }
    
    private JWTClaimsSet getClaims(SignedJWT jwt) {
        try {
            return jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            LOG.debug("Token claims are malformed");
            throw new InvalidJwtException();
        }
    }
    
    private SysRolesRequired getSysRolesRequiredAnnotation(Method method) {
        SysRolesRequired sysRoles = method.getAnnotation(SysRolesRequired.class);
        if (sysRoles == null) {
            return method.getDeclaringClass().getAnnotation(SysRolesRequired.class);
        }
        return sysRoles;
    }
    
    private boolean classAnnotatedScopes(Method method) {
        SysRolesRequired scopes = method.getAnnotation(SysRolesRequired.class);
        return scopes == null;
    }
    
    private boolean isNotPublic(Method method) {
        if (classAnnotatedScopes(method)) {
            PublicResource publicResource = method.getDeclaredAnnotation(PublicResource.class);
            return publicResource == null;
        }
        return true;
    }
    
    private void validateAuthenticated() throws UnauthorizedException {
        if (!authContext.isAuthenticated()) {
            throw new UnauthorizedException("error.unauthorized");
        }
    }
    
    private void validateSysRoles(SysRolesRequired annotation) throws UnauthorizedException, ForbiddenException {
        this.validateAuthenticated();
        
        Set<String> allowedRoles = Set.of(annotation.value());
        Set<String> userRoles = authContext.getSysRoles();
        
        boolean hasRole = !Collections.disjoint(userRoles, allowedRoles);
        if (!hasRole) {
            throw new ForbiddenException("error.forbidden");
        }
    }
}
