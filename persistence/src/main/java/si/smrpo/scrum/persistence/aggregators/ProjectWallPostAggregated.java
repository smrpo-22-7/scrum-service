package si.smrpo.scrum.persistence.aggregators;

import si.smrpo.scrum.persistence.project.ProjectWallPostEntity;

public class ProjectWallPostAggregated {
    
    private ProjectWallPostEntity post;
    
    private Integer numOfComments;
    
    public ProjectWallPostAggregated() {
    
    }
    
    public ProjectWallPostAggregated(ProjectWallPostEntity post, Integer numOfComments) {
        this.post = post;
        this.numOfComments = numOfComments;
    }
    
    public ProjectWallPostEntity getPost() {
        return post;
    }
    
    public void setPost(ProjectWallPostEntity post) {
        this.post = post;
    }
    
    public Integer getNumOfComments() {
        return numOfComments;
    }
    
    public void setNumOfComments(Integer numOfComments) {
        this.numOfComments = numOfComments;
    }
}
