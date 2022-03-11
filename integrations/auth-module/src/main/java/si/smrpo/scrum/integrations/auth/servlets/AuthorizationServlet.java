package si.smrpo.scrum.integrations.auth.servlets;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.integrations.auth.AuthConstants;
import si.smrpo.scrum.integrations.auth.services.AuthorizationService;
import si.smrpo.scrum.integrations.auth.services.SessionService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.ServletUtil;
import si.smrpo.scrum.lib.enums.ErrorCode;
import si.smrpo.scrum.lib.enums.PKCEMethod;
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
import java.util.Optional;

import static si.smrpo.scrum.integrations.auth.AuthConstants.*;
import static si.smrpo.scrum.integrations.auth.ServletConstants.LOGIN_SERVLET;

@RequestScoped
public class AuthorizationServlet extends HttpServlet {
    
    private static final Logger LOG = LogManager.getLogger(AuthorizationServlet.class.getName());
    
    @Inject
    private SessionService sessionService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processAuthorizationFlow(req, resp);
        } catch (UnauthorizedException e) {
            returnErrorToClient(req, resp, ErrorCode.UNAUTHORIZED.code());
        } catch (Exception e) {
            returnErrorToClient(req, resp, ErrorCode.SERVER_ERROR.code());
        }
    }
    
    private void processAuthorizationFlow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("Processing authorization servlet...");
        String prompt = req.getParameter(AuthConstants.PROMPT_PARAM);
        Optional<Cookie> sessionCookie = HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies());
    
        if (sessionCookie.isPresent()) {
            LOG.trace("Session cookie is present, checking for existing session");
            // If session cookie is present, check for existing session
            Optional<SessionEntity> existingSession = sessionService.getSession(sessionCookie.get().getValue(), req.getRemoteAddr());
            if (existingSession.isPresent() && existingSession.get().getUser() != null) {
                LOG.trace("Valid session already exists, authentication is not needed");
                // Session already exists and is valid, user does not need to be re-authenticated.
                handleExistingSession(req, resp, existingSession.get());
                return;
            }
            // If session cookie is present, but does not point to valid
            // session (i.e. same user ip), continue as if no cookie was set
            LOG.trace("Session cookie is present, but does not point to valid session, therefore ignoring cookie");
        }
    
        // If prompt=none, user cannot be asked for credentials and since no valid cookies
        // are provided, return error to user instead
        if (prompt != null && prompt.equals("none")) {
            LOG.trace("No valid session exists and prompt is set to 'none', therefore return error");
            returnErrorToClient(req, resp, ErrorCode.LOGIN_REQUIRED.code());
            return;
        }
    
        // No session information is provided, assume no session is connected to user agent
        // start new session (unassociated with user for now)
        LOG.trace("Starting new session");
        createSession(req, resp);
    
        // Redirect to login page
        LOG.trace("Redirecting to login page");
        Map<String, String[]> params = new HashMap<>(req.getParameterMap());
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        params.put(REQUEST_ID_PARAM, new String[]{requestId});
        resp.sendRedirect(LOGIN_SERVLET + HttpUtil.formatQueryParams(params));
    }
    
    private void createSession(HttpServletRequest req, HttpServletResponse resp) {
        SessionEntity session = sessionService.startSession(req.getRemoteAddr());
        LOG.trace("New session created");
        resp.addCookie(ServletUtil.createSessionCookie(session.getId()));
        LOG.trace("Session cookie set");
    }
    
    private void handleExistingSession(HttpServletRequest req, HttpServletResponse resp, SessionEntity session) throws IOException {
        String prompt = req.getParameter(PROMPT_PARAM);
        String pkceChallenge = req.getParameter(CODE_CHALLENGE_PARAM);
        
        // Try to perform silent authentication
        if (prompt != null && prompt.equals("none")) {
            LOG.trace("Performing silent authentication");
            AuthorizationRequestEntity request = authorizationService.initializeSessionRequest(session.getId(), req.getRemoteAddr(), pkceChallenge, PKCEMethod.S256);
            LOG.trace("Session associated with request");
            silentAuthentication(req, resp, session, request);
            return;
        } else if (prompt != null && prompt.equals("login")) {
            // Client explicitly demands a new login prompt
            LOG.debug("New login prompt demanded explicitly");
            resp.sendRedirect(LOGIN_SERVLET + HttpUtil.formatQueryParams(req.getParameterMap()));
            return;
        }
        
        // If no prompt specified otherwise, try to perform silent authentication
        LOG.trace("No prompt specified - performing silent authentication");
        AuthorizationRequestEntity request = authorizationService.initializeSessionRequest(session.getId(), req.getRemoteAddr(), pkceChallenge, PKCEMethod.S256);
        silentAuthentication(req, resp, session, request);
    }
    
    private void silentAuthentication(HttpServletRequest req, HttpServletResponse resp, SessionEntity session, AuthorizationRequestEntity request) throws IOException {
        LOG.trace("Performing silent authentication");
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        resp.addCookie(ServletUtil.createSessionCookie(session.getId()));
        resp.sendRedirect(redirectUri + ServletUtil.buildRedirectUriParams(request, session));
    }
    
    private void returnErrorToClient(HttpServletRequest req, HttpServletResponse resp, String error) throws IOException {
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        resp.sendRedirect(redirectUri + ServletUtil.buildErrorParams(error));
    }
}
