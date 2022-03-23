package si.smrpo.scrum.persistence.aggregators;

public class ProjectMembersAggregated {
    
    private long membersCount;
    
    private long scrumMastersCount;
    
    private long productOwnersCount;
    
    public ProjectMembersAggregated() {
    
    }
    
    public ProjectMembersAggregated(long membersCount, long scrumMastersCount, long productOwnersCount) {
        this.membersCount = membersCount;
        this.scrumMastersCount = scrumMastersCount;
        this.productOwnersCount = productOwnersCount;
    }
    
    public long getMembersCount() {
        return membersCount;
    }
    
    public void setMembersCount(long membersCount) {
        this.membersCount = membersCount;
    }
    
    public long getScrumMastersCount() {
        return scrumMastersCount;
    }
    
    public void setScrumMastersCount(long scrumMastersCount) {
        this.scrumMastersCount = scrumMastersCount;
    }
    
    public long getProductOwnersCount() {
        return productOwnersCount;
    }
    
    public void setProductOwnersCount(long productOwnersCount) {
        this.productOwnersCount = productOwnersCount;
    }
}
