package si.smrpo.scrum.integrations.auth.utils;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class HttpUtil {
    
    private HttpUtil() {
    
    }
    
    public static String formatQueryParams(final Map<String, String[]> params) {
        StringBuilder sb = new StringBuilder();
        if (params == null || params.size() == 0) {
            return "";
        }
        
        sb.append("?");
        boolean first = true;
        for (var p : params.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            first = false;
            
            String[] valueParts = p.getValue();
            if (valueParts.length == 0) {
                sb.append(p.getKey());
                sb.append("=");
            } else if (valueParts.length == 1) {
                sb.append(p.getKey());
                sb.append("=");
                sb.append(valueParts[0]);
            } else {
                boolean firstVal = true;
                for (int i = 0; i < valueParts.length; i++) {
                    String v = valueParts[i];
                    if (!firstVal) {
                        sb.append("&");
                    }
                    firstVal = false;
                    sb.append(p.getKey());
                    sb.append("[");
                    sb.append(i);
                    sb.append("]");
                    sb.append("=");
                    sb.append(v);
                }
            }
        }
        return sb.toString();
    }
    
    public static String encodeURI(String uri) {
        return URLEncoder.encode(uri, StandardCharsets.UTF_8)
            .replaceAll("\\+", "%20")
            .replaceAll("%21", "!")
            .replaceAll("%27", "'")
            .replaceAll("%28", "(")
            .replaceAll("%29", ")")
            .replaceAll("%7E", "~");
    }
    
    public static Optional<Cookie> getCookieByName(String name, Cookie[] cookies) {
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals(name)) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Checks whether to use secure (TLS only) cookie. First checks <code>config.session.cookie.secure</code>. If not set,
     * it will check if environment is <code>dev</code> (allowed non-secure), otherwise will default to <code>true</code>.
     * @return whether secure cookie is configured to be used
     */
    public static boolean useSecureCookie() {
        ConfigurationUtil configUtil = ConfigurationUtil.getInstance();
        return configUtil.getBoolean("config.session.cookie.secure")
            .orElseGet(() -> configUtil.get("kumuluzee.env.name")
                .map(envName -> envName.equals("prod"))
                .orElse(true));
    }
    
    public static boolean isValidRedirectUri(String redirectUri) {
        Set<String> allowedOrigins = getAllowedOrigins();
        return allowedOrigins.stream().anyMatch(redirectUri::startsWith);
    }
    
    public static Set<String> getAllowedOrigins() {
        ConfigurationUtil configUtil = ConfigurationUtil.getInstance();
        return new HashSet<>(Arrays.asList(configUtil.get("auth.web-ui.allowed-origins").orElse("")
            .split(",")))
            .stream().map(String::trim)
            .collect(Collectors.toSet());
    }
    
}
