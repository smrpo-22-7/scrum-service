package si.smrpo.scrum.integrations.auth.filters;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.smrpo.scrum.integrations.auth.services.AuthorizationService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.ServletUtil;
import si.smrpo.scrum.lib.enums.ErrorCode;
import si.smrpo.scrum.lib.enums.PKCEMethod;
import si.smrpo.scrum.persistence.auth.AuthorizationRequestEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static si.smrpo.scrum.integrations.auth.AuthConstants.*;
import static si.smrpo.scrum.integrations.auth.ServletConstants.AUTH_SERVLET;
import static si.smrpo.scrum.integrations.auth.ServletConstants.ERROR_SERVLET;

@RequestScoped
public class AuthorizationRequestFilter implements Filter {
    
    private static final Logger LOG = LogManager.getLogger(AuthorizationRequestFilter.class.getName());
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // Handle only GET & POST requests, skip otherwise
        if (!request.getMethod().equalsIgnoreCase(HttpMethod.GET) &&
            !request.getMethod().equalsIgnoreCase(HttpMethod.POST)) {
            LOG.debug("Ignoring all methods that are not GET or POST");
            chain.doFilter(request, response);
            return;
        }
        
        // Validate PKCE params are present
        String pkceChallenge = request.getParameter(CODE_CHALLENGE_PARAM);
        String pkceMethod = request.getParameter(CODE_CHALLENGE_METHOD_PARAM);
        if (pkceChallenge == null || pkceMethod == null || !pkceMethod.equals(PKCEMethod.S256.getType())) {
            LOG.debug("Missing PKCE arguments or given PKCE method is not S256");
            response.sendRedirect(ERROR_SERVLET + ServletUtil.buildErrorParams(ErrorCode.INVALID_ARGUMENTS.code(), request.getParameterMap()));
            return;
        }
        
        // If request id is not set, redirect back to itself, with new request id
        String requestId = request.getParameter(REQUEST_ID_PARAM);
        if (requestId == null) {
            LOG.debug("Request has not yet been registered.");
            AuthorizationRequestEntity newRequest = authorizationService.initializeRequest(request.getRemoteAddr(), pkceChallenge, PKCEMethod.S256);
            String queryParams = addRequestId(request, newRequest);
            LOG.debug("Registered request with id '" + newRequest.getId() + "'");
            response.sendRedirect(AUTH_SERVLET + queryParams);
            return;
        } else {
            LOG.debug("Request id is already set to '" + requestId + "'");
        }
        LOG.trace("Proceeding with filter");
        chain.doFilter(request, response);
    }
    
    private String addRequestId(HttpServletRequest request, AuthorizationRequestEntity requestEntity) {
        Map<String, String[]> params = new HashMap<>(request.getParameterMap());
        params.put(REQUEST_ID_PARAM, new String[]{requestEntity.getId()});
        return HttpUtil.formatQueryParams(params);
    }
    
    @Override
    public void destroy() {
    
    }
}
