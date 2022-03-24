package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.projects.ProjectRole;
import si.smrpo.scrum.lib.requests.CreateProjectRequest;
import si.smrpo.scrum.lib.responses.ProjectRolesCount;
import si.smrpo.scrum.persistence.project.ProjectEntity;

import java.util.Optional;
import java.util.Set;

public interface ProjectService {

    EntityList<Project> getProjects(QueryParameters queryParameters);

    EntityList<Project> getUserProjects(String userId, QueryParameters queryParameters);

    Project getProjectById(String projectId);

    Optional<ProjectEntity> getProjectEntityById(String projectId);

    Project createProject(CreateProjectRequest request);

    Project updateProject(String projectId, Project project);
    
    EntityList<ProjectMember> getProjectMembers(String projectId, QueryParameters queryParameters);

    boolean projectNameExists(String projectName);

    void changeProjectStatus(String projectId, SimpleStatus status);

    void addUserToProject(String projectId, ProjectMember member);

    void removeUserFromProject(String projectId, String userId);

    void updateUserProjectRole(String projectId, String userId, ProjectMember member);
    
    ProjectRolesCount getProjectRolesCount(String projectId);
    
    Set<ProjectRole> getAllProjectRoles();
}
