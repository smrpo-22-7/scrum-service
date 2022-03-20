package si.smrpo.scrum.integrations.auth.servlets;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.integrations.auth.services.CredentialsService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.ServletUtil;
import si.smrpo.scrum.integrations.templating.TemplatingService;
import si.smrpo.scrum.lib.enums.ErrorCode;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static si.smrpo.scrum.integrations.auth.AuthConstants.*;
import static si.smrpo.scrum.integrations.auth.ServletConstants.ERROR_SERVLET;
import static si.smrpo.scrum.integrations.auth.ServletConstants.FORGOT_PASSWORD_SERVLET;

@RequestScoped
public class ForgotPasswordServlet extends HttpServlet {
    
    private static final Logger LOG = LogManager.getLogger(ForgotPasswordServlet.class.getName());
    
    @Inject
    private TemplatingService templatingService;
    
    @Inject
    private CredentialsService credentialsService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String success = req.getParameter(SUCCESS_PARAM);
    
        try {
            ServletUtil.validateRedirectUri(redirectUri);
            
            Map<String, Object> params = new HashMap<>();
            params.put("requestId", requestId);
            params.put("redirectUri", redirectUri);
            params.put("webUiUrl", redirectUri);
            params.put("success", success);
    
            String htmlTemplate = templatingService.renderHtml("forgot-password", params);
            ServletUtil.renderHtml(htmlTemplate, resp);
        } catch (UnauthorizedException e) {
            LOG.error(e);
            resp.sendRedirect(ERROR_SERVLET +
                ServletUtil.buildErrorParams(ErrorCode.INVALID_CREDENTIALS.code())
            );
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String email = req.getParameter(EMAIL_PARAM);
        
        try {
            ServletUtil.validateRedirectUri(redirectUri);
    
            if (email != null) {
                Map<String, String[]> emailParams = new HashMap<>();
                emailParams.put(REQUEST_ID_PARAM, new String[]{requestId});
                emailParams.put(REDIRECT_URI_PARAM, new String[]{redirectUri});
        
                credentialsService.sendResetPasswordMessage(email, req.getRemoteAddr(), emailParams);
            }
    
            Map<String, String[]> params = new HashMap<>();
            params.put(SUCCESS_PARAM, new String[]{"OK"});
            params.put(REQUEST_ID_PARAM, new String[]{requestId});
            params.put(REDIRECT_URI_PARAM, new String[]{redirectUri});
    
            resp.sendRedirect(FORGOT_PASSWORD_SERVLET + HttpUtil.formatQueryParams(params));
        } catch (UnauthorizedException e) {
            LOG.error(e);
            resp.sendRedirect(ERROR_SERVLET +
                ServletUtil.buildErrorParams(ErrorCode.INVALID_CREDENTIALS.code())
            );
        }
    }
}
