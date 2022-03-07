package si.smrpo.scrum.integrations.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import si.smrpo.scrum.integrations.auth.AuthConstants;

import javax.servlet.http.HttpServletRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationGrantRequest {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("redirect_uri")
    private String redirectUri;
    
    @JsonProperty("code_verifier")
    private String codeVerifier;
    
    public AuthorizationGrantRequest() {
    
    }
    
    public AuthorizationGrantRequest(HttpServletRequest req) {
        this.code = req.getParameter(AuthConstants.AUTHORIZATION_CODE_PARAM);
        this.codeVerifier = req.getParameter(AuthConstants.CODE_VERIFIER_PARAM);
        this.redirectUri = req.getParameter(AuthConstants.REDIRECT_URI_PARAM);
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
    
    public String getCodeVerifier() {
        return codeVerifier;
    }
    
    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }
    
}
