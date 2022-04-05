package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.projects.ProjectWallPost;
import si.smrpo.scrum.persistence.project.ProjectWallPostEntity;

import java.util.Optional;

public interface ProjectWallService {
    
    EntityList<ProjectWallPost> getPosts(String projectId, QueryParameters queryParameters);
    
    ProjectWallPost getPost(String postId);
    
    void addPost(String projectId, ProjectWallPost post);
    
    void removePost(String postId);
    
    Optional<ProjectWallPostEntity> getPostEntityById(String id);
    
}
