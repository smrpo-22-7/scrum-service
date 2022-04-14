package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectRole;
import si.smrpo.scrum.lib.requests.CreateProjectRequest;
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
    
    boolean projectNameExists(String projectName);

    void changeProjectStatus(String projectId, SimpleStatus status);
    
    Set<ProjectRole> getAllProjectRoles();
}
