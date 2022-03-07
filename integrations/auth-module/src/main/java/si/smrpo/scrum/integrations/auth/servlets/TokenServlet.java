package si.smrpo.scrum.integrations.auth.servlets;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.integrations.auth.AuthConstants;
import si.smrpo.scrum.integrations.auth.models.AuthorizationGrantRequest;
import si.smrpo.scrum.integrations.auth.models.PasswordGrantRequest;
import si.smrpo.scrum.integrations.auth.models.RefreshTokenGrantRequest;
import si.smrpo.scrum.integrations.auth.models.TokenResponse;
import si.smrpo.scrum.integrations.auth.services.SecurityService;
import si.smrpo.scrum.integrations.auth.utils.ServletUtil;
import si.smrpo.scrum.lib.enums.TokenGrantType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;

@RequestScoped
public class TokenServlet extends HttpServlet {
    
    private static final Logger LOG = LogManager.getLogger(TokenServlet.class.getName());
    
    @Inject
    private SecurityService securityService;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Processing token servlet...");
        try {
            TokenResponse tokenResponse = processRequest(req);
            LOG.debug("Returning OK response");
            ServletUtil.prepareResponse(resp, Response.Status.OK.getStatusCode(), tokenResponse);
        } catch (UnauthorizedException e) {
            LOG.debug("Returning UNAUTHORIZED response", e);
            ServletUtil.prepareResponse(resp, Response.Status.UNAUTHORIZED.getStatusCode(), new HashMap<>());
        } catch (Exception e) {
            LOG.error("Error when processing token servlet", e);
            ServletUtil.prepareResponse(resp, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), new HashMap<>());
        }
    }
    
    private TokenResponse processRequest(HttpServletRequest req) {
        TokenGrantType grantType = TokenGrantType.fromString(req.getParameter(AuthConstants.GRANT_TYPE_PARAM))
            .orElseThrow(() -> new UnauthorizedException("error.oidc.tokens.invalid-grant-type"));
    
        if (grantType.equals(TokenGrantType.AUTHORIZATION_CODE)) {
            return securityService.authorizationGrant(new AuthorizationGrantRequest(req));
        } else if (grantType.equals(TokenGrantType.REFRESH_TOKEN)) {
            return securityService.refreshTokenGrant(new RefreshTokenGrantRequest(req));
        } else if (grantType.equals(TokenGrantType.PASSWORD)) {
            return securityService.passwordGrant(new PasswordGrantRequest(req));
        }
        
        throw new UnauthorizedException("error.oidc.tokens.invalid-grant-type");
    }
}
