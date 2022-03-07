package si.smrpo.scrum.integrations.auth.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import si.smrpo.scrum.integrations.auth.AuthConstants;

import javax.servlet.http.HttpServletRequest;

public class RefreshTokenGrantRequest {
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    public RefreshTokenGrantRequest() {
    
    }
    
    public RefreshTokenGrantRequest(HttpServletRequest req) {
        this.refreshToken = req.getParameter(AuthConstants.REFRESH_TOKEN_PARAM);
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
