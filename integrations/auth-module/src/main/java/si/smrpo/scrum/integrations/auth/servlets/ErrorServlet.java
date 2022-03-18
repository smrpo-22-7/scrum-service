package si.smrpo.scrum.integrations.auth.servlets;

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

import static si.smrpo.scrum.integrations.auth.AuthConstants.ERROR_PARAM;
import static si.smrpo.scrum.integrations.auth.AuthConstants.REDIRECT_URI_PARAM;

@RequestScoped
public class ErrorServlet extends HttpServlet {
    
    @Inject
    private TemplatingService templatingService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String error = req.getParameter(ERROR_PARAM);
        String translatedError = ErrorCode.fromCode(error)
            .map(ErrorCode::description)
            .orElse("Unknown error!");
    
        String redirectUri = req.getParameter(REDIRECT_URI_PARAM);
        if (redirectUri == null || !HttpUtil.isValidRedirectUri(redirectUri)) {
            redirectUri = "#";
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("error", translatedError);
        params.put("webUiUrl", redirectUri);
        
        String htmlContent = templatingService.renderHtml("error", params);
        ServletUtil.renderHtml(htmlContent, resp);
    }
}
