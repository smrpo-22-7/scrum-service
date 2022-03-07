package si.smrpo.scrum.integrations.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import si.smrpo.scrum.integrations.auth.AuthConstants;

import javax.servlet.http.HttpServletRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordGrantRequest {
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("password")
    private String password;
    
    public PasswordGrantRequest() {
    
    }
    
    public PasswordGrantRequest(HttpServletRequest req) {
        this.username = req.getParameter(AuthConstants.USERNAME_PARAM);
        this.password = req.getParameter(AuthConstants.PASSWORD_PARAM);
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
}
