package si.smrpo.scrum.integrations.auth.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.persistence.auth.AuthorizationRequestEntity;
import si.smrpo.scrum.persistence.auth.SessionEntity;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static si.smrpo.scrum.integrations.auth.AuthConstants.*;

public class ServletUtil {
    
    private static final ObjectMapper objectMapper;
    
    static  {
        objectMapper = new ObjectMapper();
    }
    
    private ServletUtil() {
    
    }
    
    public static void renderHtml(String htmlContent, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML);
        response.setStatus(Response.Status.OK.getStatusCode());
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("Pragma", "no-cache");
        try (PrintWriter pw = response.getWriter()) {
            pw.print(htmlContent);
        }
    }
    
    public static <T> void prepareResponse(HttpServletResponse resp, int status, T payload, boolean cache) throws IOException {
        prepareResponse(resp, status, payload, MediaType.APPLICATION_JSON, cache);
    }
    
    public static <T> void prepareResponse(HttpServletResponse resp, int status, T payload) throws IOException {
        prepareResponse(resp, status, payload, MediaType.APPLICATION_JSON, true);
    }
    
    public static <T> void prepareResponse(HttpServletResponse resp, int status, T payload, String mediaType, boolean cache) throws IOException {
        resp.setContentType(mediaType);
        resp.setStatus(status);
        if (cache) {
            resp.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
            resp.setHeader("Pragma", "no-cache");
        }
        
        String serializedPayload = objectMapper.writeValueAsString(payload);
        try (PrintWriter pw = resp.getWriter()) {
            pw.print(serializedPayload);
        }
    }
    
    public static Cookie createSessionCookie(String sessionId) {
        Cookie cookie = new Cookie(SESSION_COOKIE, sessionId);
        cookie.setSecure(HttpUtil.useSecureCookie());
        int expireInMinutes = ConfigurationUtil.getInstance()
            .getInteger("config.session.expiration")
            .orElse(60); // Defaults to 1 hour
        cookie.setMaxAge(expireInMinutes * 60);
        cookie.setPath("/");
        return cookie;
    }
    
    public static Cookie clearSessionCookie() {
        Cookie cookie = new Cookie(SESSION_COOKIE, "");
        cookie.setSecure(HttpUtil.useSecureCookie());
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
    
    public static String buildRedirectUriParams(AuthorizationRequestEntity request, SessionEntity session) {
        Map<String, String[]> params = new HashMap<>();
        params.put(REQUEST_ID_PARAM, new String[]{request.getId()});
        params.put(AUTHORIZATION_CODE_PARAM, new String[]{request.getCode()});
        params.put(SESSION_STATE_PARAM, new String[]{session.getId()});
        return HttpUtil.formatQueryParams(params);
    }
    
    public static String buildErrorParams(String errorMessage) {
        return buildErrorParams(errorMessage, null, null, new HashMap<>());
    }
    
    public static String buildErrorParams(String errorMessage, String requestId) {
        return buildErrorParams(errorMessage, requestId, null, new HashMap<>());
    }
    
    public static String buildErrorParams(String errorMessage, String requestId, Map<String, String[]> keptParams) {
        return buildErrorParams(errorMessage, requestId, null, keptParams);
    }
    
    public static String buildErrorParams(String errorMessage, String requestId, String sessionId, Map<String, String[]> keptParams) {
        Map<String, String[]> params = new HashMap<>(keptParams);
        params.put(ERROR_PARAM, new String[]{HttpUtil.encodeURI(errorMessage)});
        if (requestId != null) {
            params.put(REQUEST_ID_PARAM, new String[]{requestId});
        }
        if (sessionId != null) {
            params.put(SESSION_STATE_PARAM, new String[]{sessionId});
        }
        return HttpUtil.formatQueryParams(params);
    }
    
    public static String buildErrorParams(String errorMessage, Map<String, String[]> keptParams) {
        return buildErrorParams(errorMessage, null, null, keptParams);
    }
    
    public static void validateRedirectUri(String redirectUri) throws UnauthorizedException {
        if (redirectUri == null) {
            throw new UnauthorizedException("error.unauthorized");
        } else {
            if (!HttpUtil.isValidRedirectUri(redirectUri)) {
                throw new UnauthorizedException("error.unauthorized");
            }
        }
    }
}
