package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryOrder;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterOperation;
import com.kumuluz.ee.rest.enums.OrderDirection;
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
import si.smrpo.scrum.lib.requests.ProjectRequest;
import si.smrpo.scrum.mappers.ProjectMapper;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.identifiers.ProjectUserId;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.project.ProjectRoleEntity;
import si.smrpo.scrum.persistence.project.ProjectUserEntity;
import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.services.ProjectMembershipService;
import si.smrpo.scrum.services.ProjectService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class ProjectServiceImpl implements ProjectService {
    
    private static final Logger LOG = LogManager.getLogger(ProjectServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Inject
    private ProjectMembershipService projMems;
    
    @Inject
    private Validator validator;
    
    @Override
    public EntityList<Project> getProjects(QueryParameters queryParameters) {
        QueryUtil.setDefaultFilterParam(new QueryFilter("status", FilterOperation.EQ, SimpleStatus.ACTIVE.name()), queryParameters);
        QueryUtil.setDefaultOrderParam(new QueryOrder("name", OrderDirection.ASC), queryParameters);
        
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
    public Project createProject(ProjectRequest request) {
        validator.assertNotBlank(request.getName());
        if (projectNameExists(request.getName())) {
            throw new ConflictException("error.conflict");
        }
        validator.assertNotNull(request.getMembers());
        // Check that only one role is present amongst members
        validateRequestRoles(request);
        
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
            }).forEach(e -> em.persist(e));
            
            em.getTransaction().commit();
            return ProjectMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Project updateProject(String projectId, ProjectRequest request) {
        validator.assertNotBlank(request.getName(), "name", "ProjectRequest");
        validator.assertNotNull(request.getMembers());
        // Check that only one role is present amongst members
        validateRequestRoles(request);
        
        ProjectEntity entity = getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (!request.getName().equals(entity.getName())) {
            if (projectNameExists(request.getName())) {
                throw new ConflictException("error.conflict", "ProjectRequest", "name");
            }
        }
        
        Map<String, ProjectRoleEntity> projectRolesMap = getProjectRoles();
        List<ProjectUserEntity> currentMembers = projMems.getProjectMembershipEntities(projectId);
        Map<String, ProjectUserEntity> currentMembersMap = currentMembers.stream()
            .collect(Collectors.toMap(e -> e.getUser().getId(), e -> e));
        List<ProjectMember> newMembers = request.getMembers();
        Map<String, ProjectMember> newMembersMap = request.getMembers().stream()
            .collect(Collectors.toMap(ProjectMember::getUserId, e -> e));
        
        Set<ProjectUserEntity> toBeAdded = new HashSet<>();
        Set<ProjectUserEntity> toBeDeleted = new HashSet<>();
        
        try {
            em.getTransaction().begin();
            entity.setName(request.getName());
            
            // update project membership
            for (ProjectUserEntity currentMember : currentMembers) {
                // if member is present in current and new, check role and update if needed, otherwise noop
                if (newMembersMap.containsKey(currentMember.getUser().getId())) {
                    ProjectMember newMember = newMembersMap.get(currentMember.getUser().getId());
                    if (!currentMember.getProjectRole().getRoleId().equals(newMember.getProjectRoleId())) {
                        currentMember.setProjectRole(projectRolesMap.get(newMember.getProjectRoleId()));
                        em.merge(currentMember);
                    }
                } else {
                    // if member is present in current, but not new, mark it for deletion
                    toBeDeleted.add(currentMember);
                }
            }
            for (ProjectMember newMember : newMembers) {
                // if member is present in new and not in current, mark it for addition
                if (!currentMembersMap.containsKey(newMember.getUserId())) {
                    ProjectUserEntity membership = new ProjectUserEntity();
                    membership.setProjectRole(projectRolesMap.get(newMember.getProjectRoleId()));
                    ProjectUserId membershipId = new ProjectUserId();
                    membershipId.setProject(entity);
                    UserEntity user = userService.getUserEntityById(newMember.getUserId())
                            .orElseThrow(() -> new NotFoundException("error.not-found"));
                    membershipId.setUser(user);
                    membership.setId(membershipId);
                    toBeAdded.add(membership);
                }
                // inverse condition is already checked in previous step
            }
            
            toBeDeleted.forEach(e -> em.remove(e));
            toBeAdded.forEach(e -> em.persist(e));
            
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
        QueryParameters queryParameters = new QueryParameters();
        QueryUtil.setDefaultOrderParam(new QueryOrder("roleId", OrderDirection.ASC), queryParameters);
        return JPAUtils.getEntityStream(em, ProjectRoleEntity.class, queryParameters)
            .collect(Collectors.toSet());
    }
    
    private Map<String, ProjectRoleEntity> getProjectRoles() {
        return JPAUtils.getEntityStream(em, ProjectRoleEntity.class, new QueryParameters())
            .collect(Collectors.toMap(ProjectRoleEntity::getRoleId, e -> e));
    }
    
    /**
     * Checks that only one role is present and that project has assigned members
     */
    private void validateRequestRoles(ProjectRequest request) throws ValidationException {
        Map<String, Integer> rolesCount = request.getMembers().stream()
            .collect(Collectors.toMap(ProjectMember::getProjectRoleId, e -> 1, Integer::sum));
        
        if (rolesCount.getOrDefault(ProjectRoleEntity.PROJECT_ROLE_PRODUCT_OWNER, 0) != 1) {
            throw new ValidationException("error.project.roles.limit.product-owner")
                .withEntity("ProjectRequest")
                .withField("members")
                .withDescription("Only one product owner per project is allowed!");
        }
        if (rolesCount.getOrDefault(ProjectRoleEntity.PROJECT_ROLE_SCRUM_MASTER, 0) != 1) {
            throw new ValidationException("error.project.roles.limit.scrum-master")
                .withEntity("ProjectRequest")
                .withField("members")
                .withDescription("Only one scrum master per project is allowed!");
        }
        if (rolesCount.getOrDefault(ProjectRoleEntity.PROJECT_ROLE_MEMBER, 0) == 0) {
            throw new ValidationException("error.project.roles.limit.members")
                .withEntity("ProjectRequest")
                .withField("members")
                .withDescription("Project must have at least one member!");
        }
    }
}
