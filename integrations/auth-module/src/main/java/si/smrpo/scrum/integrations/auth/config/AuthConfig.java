package si.smrpo.scrum.integrations.auth.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle(".")
@ApplicationScoped
public class AuthConfig {
    
    @ConfigValue("kumuluzee.server.base-url")
    private String baseUrl;
    
    @ConfigValue("web-ui.url")
    private String webClientUrl;
    
    @ConfigValue("web-ui.oidc-callback")
    private String clientRedirectUri;
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getWebClientUrl() {
        return webClientUrl;
    }
    
    public void setWebClientUrl(String webClientUrl) {
        this.webClientUrl = webClientUrl;
    }
    
    public String getClientRedirectUri() {
        return clientRedirectUri;
    }
    
    public void setClientRedirectUri(String clientRedirectUri) {
        this.clientRedirectUri = clientRedirectUri;
    }
}
