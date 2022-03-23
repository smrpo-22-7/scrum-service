package si.smrpo.scrum.lib.responses;

public class ProjectRolesCount {
    
    private String projectId;
    
    private Long membersCount;
    
    private Long productOwnersCount;
    
    private Long scrumMastersCount;
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public Long getMembersCount() {
        return membersCount;
    }
    
    public void setMembersCount(Long membersCount) {
        this.membersCount = membersCount;
    }
    
    public Long getProductOwnersCount() {
        return productOwnersCount;
    }
    
    public void setProductOwnersCount(Long productOwnersCount) {
        this.productOwnersCount = productOwnersCount;
    }
    
    public Long getScrumMastersCount() {
        return scrumMastersCount;
    }
    
    public void setScrumMastersCount(Long scrumMastersCount) {
        this.scrumMastersCount = scrumMastersCount;
    }
}
