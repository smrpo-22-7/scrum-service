package si.smrpo.scrum.integrations.auth.servlets;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.integrations.auth.config.AuthConfig;
import si.smrpo.scrum.integrations.auth.services.AuthorizationService;
import si.smrpo.scrum.integrations.auth.services.SessionService;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.ServletUtil;
import si.smrpo.scrum.integrations.templating.TemplatingService;
import si.smrpo.scrum.lib.enums.ErrorCode;
import si.smrpo.scrum.persistence.auth.AuthorizationRequestEntity;
import si.smrpo.scrum.persistence.auth.SessionEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static si.smrpo.scrum.integrations.auth.AuthConstants.*;
import static si.smrpo.scrum.integrations.auth.ServletConstants.LOGIN_SERVLET;

@RequestScoped
public class LoginServlet extends HttpServlet {
    
    private static final Logger LOG = LogManager.getLogger(LoginServlet.class.getName());
    
    @Inject
    private AuthConfig authConfig;
    
    @Inject
    private UserService userService;
    
    @Inject
    private SessionService sessionService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Inject
    private TemplatingService templatingService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Processing GET login servlet...");
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String error = req.getParameter(ERROR_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
    
        Map<String, Object> params = new HashMap<>();
        params.put("requestId", requestId);
        params.put("error", error);
        params.put("redirectUri", redirectUri);
        String htmlContent = templatingService.renderHtml("login", params);
    
        ServletUtil.renderHtml(htmlContent, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Processing POST login servlet...");
        String username = req.getParameter(USERNAME_PARAM);
        String password = req.getParameter(PASSWORD_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
    
        try {
            if (redirectUri == null) {
                LOG.debug("Redirect URI is not set. Returning error");
                throw new UnauthorizedException("error.unauthorized");
            } else {
                if (!HttpUtil.isValidRedirectUri(redirectUri)) {
                    LOG.debug("Redirect URI is not valid!");
                    throw new UnauthorizedException("error.unauthorized");
                }
            }
            
            if (requestId == null) {
                LOG.debug("Request id is not set. Returning error");
                throw new UnauthorizedException("error.unauthorized");
            }
            
            LOG.trace("Checking user credentials...");
            UserEntity user = userService.checkUserCredentials(username, password);
            LOG.debug("User credentials OK");
            // Session should already exist at this point
            Cookie sessionCookie = HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies())
                .orElseThrow(() -> {
                    LOG.debug("Invalid session state!");
                    return new UnauthorizedException("error.unauthorized");
                });
            LOG.trace("Session already exists, retrieved session cookie");
            SessionEntity session = sessionService.associateUserWithSession(sessionCookie.getValue(), user.getId());
            LOG.trace("Associated user with session");
            resp.addCookie(sessionCookie);
            
            redirectSuccessfullyBackToClient(redirectUri, resp, requestId, user.getId(), session);
        } catch (UnauthorizedException e) {
            LOG.trace(e);
            resp.sendRedirect(LOGIN_SERVLET + ServletUtil.buildErrorParams(ErrorCode.INVALID_CREDENTIALS.code(), req.getParameterMap()));
        } catch (Exception e) {
            LOG.error(e);
            resp.sendRedirect(LOGIN_SERVLET + ServletUtil.buildErrorParams(ErrorCode.SERVER_ERROR.code(), req.getParameterMap()));
        }
    }
    
    private void redirectSuccessfullyBackToClient(String redirectUrl, HttpServletResponse resp, String requestId, String userId, SessionEntity session) throws IOException {
        String redirectUri = redirectUrl + authConfig.getClientRedirectUri();
        LOG.trace("Redirecting back to client with authorization code");
        AuthorizationRequestEntity request = authorizationService.createAuthorizationCode(requestId, userId);
        resp.sendRedirect(redirectUri + ServletUtil.buildRedirectUriParams(request, session));
    }
}
