package si.smrpo.scrum.lib.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import si.smrpo.scrum.lib.sprints.Sprint;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SprintListResponse {
    
    private Sprint activeSprint;
    
    private List<Sprint> pastSprints;
    
    private List<Sprint> futureSprints;
    
    public Sprint getActiveSprint() {
        return activeSprint;
    }
    
    public void setActiveSprint(Sprint activeSprint) {
        this.activeSprint = activeSprint;
    }
    
    public List<Sprint> getPastSprints() {
        return pastSprints;
    }
    
    public void setPastSprints(List<Sprint> pastSprints) {
        this.pastSprints = pastSprints;
    }
    
    public List<Sprint> getFutureSprints() {
        return futureSprints;
    }
    
    public void setFutureSprints(List<Sprint> futureSprints) {
        this.futureSprints = futureSprints;
    }
}
