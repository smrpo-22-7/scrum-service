package si.smrpo.scrum.integrations.auth.servlets;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.smrpo.scrum.integrations.auth.services.SessionService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.ServletUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static si.smrpo.scrum.integrations.auth.AuthConstants.POST_LOGOUT_REDIRECT_URI_PARAM;
import static si.smrpo.scrum.integrations.auth.AuthConstants.SESSION_COOKIE;

@RequestScoped
public class EndSessionServlet extends HttpServlet {
    
    private static final Logger LOG = LogManager.getLogger(EndSessionServlet.class.getName());
    
    @Inject
    private SessionService sessionService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<Cookie> sessionCookie = HttpUtil.getCookieByName(SESSION_COOKIE, req.getCookies());
    
        sessionCookie.ifPresent(cookie -> {
            LOG.debug("Session present, deleting it...");
            sessionService.endSession(cookie.getValue());
            resp.addCookie(ServletUtil.clearSessionCookie());
            LOG.debug("Removed existing sessions");
        });
    
        String postLogoutUri = req.getParameter(POST_LOGOUT_REDIRECT_URI_PARAM);
        if (postLogoutUri != null) {
            LOG.debug("Redirecting back to post logout uri");
            resp.sendRedirect(postLogoutUri);
            return;
        }
    
        resp.setStatus(204);
    }
}
