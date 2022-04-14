package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterOperation;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.ConflictException;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.ValidationException;
import com.mjamsek.rest.services.Validator;
import com.mjamsek.rest.utils.QueryUtil;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.projects.ProjectRole;
import si.smrpo.scrum.lib.requests.CreateProjectRequest;
import si.smrpo.scrum.lib.responses.ProjectRolesCount;
import si.smrpo.scrum.mappers.ProjectMapper;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.identifiers.ProjectUserId;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.project.ProjectRoleEntity;
import si.smrpo.scrum.persistence.project.ProjectUserEntity;
import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.services.ProjectService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestScoped
public class ProjectServiceImpl implements ProjectService {
    
    private static final Logger LOG = LogManager.getLogger(ProjectServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Inject
    private Validator validator;
    
    @Override
    public EntityList<Project> getProjects(QueryParameters queryParameters) {
        QueryUtil.setDefaultFilterParam(new QueryFilter("status", FilterOperation.EQ, SimpleStatus.ACTIVE.name()), queryParameters);
        
        List<Project> projects = JPAUtils.getEntityStream(em, ProjectEntity.class, queryParameters)
            .map(ProjectMapper::fromEntity)
            .collect(Collectors.toList());
        
        long projectCount = JPAUtils.queryEntitiesCount(em, ProjectEntity.class, queryParameters);
        
        return new EntityList<>(projects, projectCount);
    }
    
    @Override
    public EntityList<Project> getUserProjects(String userId, QueryParameters queryParameters) {
        TypedQuery<ProjectEntity> query = em.createNamedQuery(ProjectEntity.GET_USER_PROJECTS, ProjectEntity.class);
        query.setParameter("userId", userId);
        query.setMaxResults(Math.toIntExact(queryParameters.getLimit()));
        query.setFirstResult(Math.toIntExact(queryParameters.getOffset()));
        
        TypedQuery<Long> countQuery = em.createNamedQuery(ProjectEntity.COUNT_USER_PROJECT, Long.class);
        countQuery.setParameter("userId", userId);
        
        try {
            List<Project> projects = query.getResultStream()
                .map(ProjectMapper::fromEntity)
                .collect(Collectors.toList());
            
            Long projectCount = countQuery.getSingleResult();
            return new EntityList<>(projects, projectCount);
        } catch (NoResultException e) {
            return new EntityList<>();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Project getProjectById(String projectId) {
        return getProjectEntityById(projectId)
            .map(ProjectMapper::fromEntity)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
    }
    
    @Override
    public Optional<ProjectEntity> getProjectEntityById(String projectId) {
        return Optional.ofNullable(em.find(ProjectEntity.class, projectId));
    }
    
    @Override
    public Project createProject(CreateProjectRequest request) {
        
        validator.assertNotBlank(request.getName());
        if (projectNameExists(request.getName())) {
            throw new ConflictException("error.conflict");
        }
        
        
        // Check that only one role is present amongst members
        ProjectRolesCount rolesCount = getRolesCountFromRequest(request);
        if (rolesCount.getProductOwnersCount() > 1) {
            throw new ValidationException("error.project.roles.limit.product-owner");
        }
        if (rolesCount.getScrumMastersCount() > 1) {
            throw new ValidationException("error.project.roles.limit.product-owner");
        }
        
        ProjectEntity entity = new ProjectEntity();
        entity.setStatus(SimpleStatus.ACTIVE);
        entity.setName(request.getName());
        
        Map<String, ProjectRoleEntity> projectRolesMap = getProjectRoles();
        List<String> userIds = request.getMembers().stream()
            .map(ProjectMember::getUserId)
            .collect(Collectors.toList());
        Map<String, UserEntity> usersMap = userService.getUserEntitiesByIds(userIds)
            .stream()
            .collect(Collectors.toMap(BaseEntity::getId, user -> user));
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            
            request.getMembers().stream().map(member -> {
                ProjectUserEntity projectUserEntity = new ProjectUserEntity();
                ProjectUserId projectUserId = new ProjectUserId();
                projectUserId.setProject(entity);
                projectUserId.setUser(usersMap.get(member.getUserId()));
                projectUserEntity.setId(projectUserId);
                projectUserEntity.setProjectRole(projectRolesMap.get(member.getProjectRoleId()));
                return projectUserEntity;
            }).forEach(projectUserEntity -> {
                em.persist(projectUserEntity);
            });
            
            em.getTransaction().commit();
            return ProjectMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Project updateProject(String projectId, Project project) {
        ProjectEntity entity = getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        validator.assertNotBlank(project.getName());
        if (projectNameExists(project.getName())) {
            throw new ConflictException("error.conflict");
        }
        
        try {
            em.getTransaction().begin();
            entity.setName(project.getName());
            em.getTransaction().commit();
            return ProjectMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public boolean projectNameExists(String projectName) {
        TypedQuery<ProjectEntity> query = em.createNamedQuery(ProjectEntity.GET_BY_PROJECT_NAME, ProjectEntity.class);
        query.setParameter("name", projectName.toLowerCase());
        
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void changeProjectStatus(String projectId, SimpleStatus status) {
        ProjectEntity entity = getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        try {
            em.getTransaction().begin();
            entity.setStatus(status);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Set<ProjectRole> getAllProjectRoles() {
        return getAllProjectRoleEntities().stream()
            .map(ProjectMapper::fromEntity)
            .collect(Collectors.toSet());
    }
    
    private Set<ProjectRoleEntity> getAllProjectRoleEntities() {
        return JPAUtils.getEntityStream(em, ProjectRoleEntity.class, new QueryParameters())
            .collect(Collectors.toSet());
    }
    
    private Map<String, ProjectRoleEntity> getProjectRoles() {
        return JPAUtils.getEntityStream(em, ProjectRoleEntity.class, new QueryParameters())
            .collect(Collectors.toMap(ProjectRoleEntity::getRoleId, e -> e));
    }
    
    private ProjectRolesCount getRolesCountFromRequest(CreateProjectRequest request) {
        ProjectRolesCount counter = new ProjectRolesCount();
        counter.setProductOwnersCount(countRoles(request.getMembers(), ProjectRoleEntity.PROJECT_ROLE_PRODUCT_OWNER));
        counter.setScrumMastersCount(countRoles(request.getMembers(), ProjectRoleEntity.PROJECT_ROLE_SCRUM_MASTER));
        counter.setMembersCount(countRoles(request.getMembers(), ProjectRoleEntity.PROJECT_ROLE_MEMBER));
        return counter;
    }
    
    private long countRoles(List<ProjectMember> members, String roleId) {
        return members.stream().reduce(0L, (acc, elem) -> {
            if (elem.getProjectRoleId().equals(roleId)) {
                return acc + 1;
            }
            return acc;
        }, Long::sum);
    }
    
}
