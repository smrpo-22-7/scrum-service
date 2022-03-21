package si.smrpo.scrum.mappers;

import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectRole;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.project.ProjectRoleEntity;

public class ProjectMapper {

    public static Project fromEntity(ProjectEntity entity) {
        Project project = BaseMapper.fromEntity(entity, Project.class);

        project.setStatus(entity.getStatus());
        project.setName(entity.getName());

        return project;
    }
    
    public static ProjectRole fromEntity(ProjectRoleEntity entity) {
        ProjectRole role = BaseMapper.fromEntity(entity, ProjectRole.class);
        role.setRoleId(entity.getRoleId());
        role.setName(entity.getName());
        return role;
    }

}
