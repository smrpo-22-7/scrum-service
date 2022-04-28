package si.smrpo.scrum.lib.ws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocketMessage {
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("accessToken")
    private String accessToken;
    
    @JsonProperty("payloadType")
    private String payloadType;
    
    @JsonProperty("payload")
    private String payload;
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getPayloadType() {
        return payloadType;
    }
    
    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
    }
    
    public String getPayload() {
        return payload;
    }
    
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
