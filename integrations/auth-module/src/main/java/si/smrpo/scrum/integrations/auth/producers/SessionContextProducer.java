package si.smrpo.scrum.integrations.auth.producers;

import si.smrpo.scrum.integrations.auth.models.SessionContext;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static si.smrpo.scrum.integrations.auth.AuthConstants.SESSION_COOKIE;

@RequestScoped
public class SessionContextProducer {
    
    @Inject
    private HttpServletRequest httpRequest;
    
    @Produces
    @RequestScoped
    public SessionContext produceSessionContext() {
        return HttpUtil.getCookieByName(SESSION_COOKIE, httpRequest.getCookies())
            .map(Cookie::getValue)
            .map(SessionContext::new)
            .orElseGet(SessionContext::new);
    }
    
}
