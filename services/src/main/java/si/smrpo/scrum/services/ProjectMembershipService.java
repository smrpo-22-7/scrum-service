package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.responses.ProjectRolesCount;
import si.smrpo.scrum.persistence.project.ProjectUserEntity;

import java.util.List;

public interface ProjectMembershipService {
    
    List<ProjectUserEntity> getProjectMembershipEntities(String projectId);
    
    EntityList<ProjectMember> getProjectMembers(String projectId, QueryParameters queryParameters);
    
    List<UserProfile> queryProjectMembers(String projectId, String query);
    
    void addUserToProject(String projectId, ProjectMember member);
    
    void removeUserFromProject(String projectId, String userId);
    
    void updateUserProjectRole(String projectId, String userId, ProjectMember member);
    
    ProjectRolesCount getProjectRolesCount(String projectId);
}
