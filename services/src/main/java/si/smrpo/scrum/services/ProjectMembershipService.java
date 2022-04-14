package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.responses.ProjectRolesCount;

import java.util.List;

public interface ProjectMembershipService {
    
    EntityList<ProjectMember> getProjectMembers(String projectId, QueryParameters queryParameters);
    
    List<UserProfile> queryProjectMembers(String projectId, String query);
    
    void addUserToProject(String projectId, ProjectMember member);
    
    void removeUserFromProject(String projectId, String userId);
    
    void updateUserProjectRole(String projectId, String userId, ProjectMember member);
    
    ProjectRolesCount getProjectRolesCount(String projectId);
}
