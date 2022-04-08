package si.smrpo.scrum.integrations.auth.servlets;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.integrations.auth.services.AuthorizationService;
import si.smrpo.scrum.integrations.auth.services.SessionService;
import si.smrpo.scrum.integrations.auth.services.TwoFactorAuthenticationService;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.ServletUtil;
import si.smrpo.scrum.integrations.templating.TemplatingService;
import si.smrpo.scrum.lib.enums.ErrorCode;
import si.smrpo.scrum.persistence.auth.AuthorizationRequestEntity;
import si.smrpo.scrum.persistence.auth.SessionEntity;

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
import static si.smrpo.scrum.integrations.auth.ServletConstants.ERROR_SERVLET;
import static si.smrpo.scrum.integrations.auth.ServletConstants.TWO_FA_SERVLET;

@RequestScoped
public class TwoFactorAuthenticationServlet extends HttpServlet {
    
    private static final Logger LOG = LogManager.getLogger(TwoFactorAuthenticationServlet.class.getName());
    
    @Inject
    private SessionService sessionService;
    
    @Inject
    private TwoFactorAuthenticationService twoFactorAuthenticationService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private TemplatingService templatingService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter(CODE_2FA_PARAM);
        String challenge = req.getParameter(CHALLENGE_2FA_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String error = req.getParameter(ERROR_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        
        try {
            if (challenge == null || code == null) {
                LOG.debug("Processing GET 2FA servlet...");
    
                Cookie sessionCookie = HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies())
                    .orElseThrow(() -> {
                        LOG.debug("Invalid session state!");
                        return new UnauthorizedException("error.unauthorized");
                    });
    
                sessionService.getSession(sessionCookie.getValue(), req.getRemoteAddr())
                    .orElseThrow(() -> {
                        LOG.debug("Invalid session state!");
                        return new UnauthorizedException("error.unauthorized");
                    });
                
                try {
                    ServletUtil.validateRedirectUri(redirectUri);
                } catch (UnauthorizedException e) {
                    resp.sendRedirect(ERROR_SERVLET + ServletUtil.buildErrorParams(ErrorCode.MISSING_REQUIRED_FIELDS.code(), requestId));
                    return;
                }
                
                if (error != null) {
                    error = ErrorCode.fromCode(error)
                        .map(ErrorCode::description)
                        .orElse("Unknown error!");
                }
                
                Map<String, Object> params = new HashMap<>();
                params.put("requestId", requestId);
                params.put("error", error);
                params.put("redirectUri", redirectUri);
                params.put("webUiUrl", redirectUri);
                
                String htmlContent = templatingService.renderHtml("2fa", params);
                ServletUtil.renderHtml(htmlContent, resp);
            } else {
                this.doPost(req, resp);
            }
        } catch (UnauthorizedException e) {
            resp.sendRedirect(TWO_FA_SERVLET +
                ServletUtil.buildErrorParams(
                    ErrorCode.INVALID_CREDENTIALS.code(),
                    sanitizeParameters(req.getParameterMap())
                ));
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter(CODE_2FA_PARAM);
        String challenge = req.getParameter(CHALLENGE_2FA_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        
        // If code or challenge are not set, throw error
        if (code == null) {
            resp.sendRedirect(ERROR_SERVLET +
                ServletUtil.buildErrorParams(ErrorCode.MISSING_REQUIRED_FIELDS.code()));
            return;
        }
        
        try {
            ServletUtil.validateRedirectUri(redirectUri);
        } catch (UnauthorizedException e) {
            resp.sendRedirect(ERROR_SERVLET + ServletUtil.buildErrorParams(ErrorCode.MISSING_REQUIRED_FIELDS.code(), requestId));
            return;
        }
        
        try {
            Cookie sessionCookie = HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies()).orElseThrow(() -> {
                LOG.debug("Invalid session state!");
                return new UnauthorizedException("error.unauthorized");
            });
            
            SessionEntity session = sessionService.getSession(sessionCookie.getValue(), req.getRemoteAddr())
                .orElseThrow(() -> {
                    LOG.debug("Invalid session state!");
                    return new UnauthorizedException("error.unauthorized");
                });
            
            boolean validCode = twoFactorAuthenticationService.verify2FAChallenge(code.trim(), challenge, session.getId());
            if (!validCode) {
                resp.sendRedirect(TWO_FA_SERVLET +
                    ServletUtil.buildErrorParams(
                        ErrorCode.INVALID_CREDENTIALS.code(),
                        sanitizeParameters(req.getParameterMap())
                    ));
                return;
            }
            
            sessionService.activateSession(sessionCookie.getValue());
            userService.saveLoginEvent(session.getUser().getId());
            redirectSuccessfullyBackToClient(redirectUri, resp, requestId, session.getUser().getId(), session);
        } catch (UnauthorizedException e) {
            resp.sendRedirect(TWO_FA_SERVLET +
                ServletUtil.buildErrorParams(
                    ErrorCode.INVALID_CREDENTIALS.code(),
                    sanitizeParameters(req.getParameterMap())
                ));
        }
    }
    
    private void redirectSuccessfullyBackToClient(String redirectUrl, HttpServletResponse resp, String requestId, String userId, SessionEntity session) throws IOException {
        LOG.trace("Redirecting back to client with authorization code");
        AuthorizationRequestEntity request = authorizationService.createAuthorizationCode(requestId, userId);
        resp.sendRedirect(redirectUrl + ServletUtil.buildRedirectUriParams(request, session));
    }
    
    private Map<String, String[]> sanitizeParameters(Map<String, String[]> paramsMap) {
        Map<String, String[]> params = new HashMap<>(paramsMap);
        params.remove(CODE_2FA_PARAM);
        return params;
    }
}
