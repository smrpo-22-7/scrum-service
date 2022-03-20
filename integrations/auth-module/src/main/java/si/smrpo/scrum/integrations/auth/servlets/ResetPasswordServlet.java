package si.smrpo.scrum.integrations.auth.servlets;

import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.integrations.auth.services.CredentialsService;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.ServletUtil;
import si.smrpo.scrum.integrations.templating.TemplatingService;
import si.smrpo.scrum.lib.enums.ErrorCode;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static si.smrpo.scrum.integrations.auth.AuthConstants.*;
import static si.smrpo.scrum.integrations.auth.ServletConstants.ERROR_SERVLET;
import static si.smrpo.scrum.integrations.auth.ServletConstants.RESET_PASSWORD_SERVLET;

@RequestScoped
public class ResetPasswordServlet extends HttpServlet {
    
    @Inject
    private TemplatingService templatingService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private CredentialsService credentialsService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String code = req.getParameter(CHALLENGE_RESET_PASSWORD_PARAM);
        String error = req.getParameter(ERROR_PARAM);
        
        try {
            ServletUtil.validateRedirectUri(redirectUri);
            
            Map<String, Object> params = new HashMap<>();
            params.put("error", error);
            params.put("requestId", requestId);
            params.put("redirectUri", redirectUri);
            params.put("webUiUrl", redirectUri);
            params.put("resetChallenge", code);
            
            String htmlContent = templatingService.renderHtml("reset-password", params);
            ServletUtil.renderHtml(htmlContent, resp);
        } catch (UnauthorizedException e) {
            resp.sendRedirect(ERROR_SERVLET +
                ServletUtil.buildErrorParams(ErrorCode.INVALID_CREDENTIALS.code())
            );
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        String requestId = req.getParameter(REQUEST_ID_PARAM);
        String code = req.getParameter(CHALLENGE_RESET_PASSWORD_PARAM);
        String password = req.getParameter(PASSWORD_PARAM);
        String confirmPassword = req.getParameter(CONFIRM_PASSWORD_PARAM);
        
        try {
            ServletUtil.validateRedirectUri(redirectUri);
    
            boolean isValidPassword = userService.isValidPassword(password, confirmPassword);
            if (!isValidPassword) {
                resp.sendRedirect(RESET_PASSWORD_SERVLET + ServletUtil.buildErrorParams(ErrorCode.PASSWORD_MISMATCH.code(), req.getParameterMap()));
                return;
            }
    
            Optional<UserEntity> validatedUser = credentialsService.validateResetPasswordChallenge(code, req.getRemoteAddr());
            if (validatedUser.isEmpty()) {
                resp.sendRedirect(ERROR_SERVLET + ServletUtil.buildErrorParams(ErrorCode.INVALID_CREDENTIALS.code()));
                return;
            }
    
            userService.setPassword(validatedUser.get().getId(), password);
            
            Map<String, String[]> redirectParams = new HashMap<>();
            redirectParams.put(REQUEST_ID_PARAM, new String[]{requestId});
            resp.sendRedirect(redirectUri + HttpUtil.formatQueryParams(redirectParams));
        } catch (UnauthorizedException e) {
            resp.sendRedirect(ERROR_SERVLET +
                ServletUtil.buildErrorParams(ErrorCode.INVALID_CREDENTIALS.code())
            );
        }
    }
    
}
