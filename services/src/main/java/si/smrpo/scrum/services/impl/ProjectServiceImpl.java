package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterOperation;
import com.kumuluz.ee.rest.interfaces.CriteriaFilter;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.*;
import com.mjamsek.rest.services.Validator;
import com.mjamsek.rest.utils.QueryUtil;
import si.smrpo.scrum.integrations.auth.mappers.UserMapper;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.projects.ProjectRole;
import si.smrpo.scrum.lib.requests.CreateProjectRequest;
import si.smrpo.scrum.lib.responses.ProjectRolesCount;
import si.smrpo.scrum.mappers.ProjectMapper;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.aggregators.ProjectMembersAggregated;
import si.smrpo.scrum.persistence.identifiers.ProjectUserId;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.project.ProjectRoleEntity;
import si.smrpo.scrum.persistence.project.ProjectUserEntity;
import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.services.ProjectService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
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
    public EntityList<ProjectMember> getProjectMembers(String projectId, QueryParameters queryParameters) {
        TypedQuery<ProjectUserEntity> query = em.createNamedQuery(ProjectUserEntity.GET_PROJECT_MEMBERS, ProjectUserEntity.class);
        query.setParameter("projectId", projectId);
        query.setMaxResults(Math.toIntExact(queryParameters.getLimit()));
        query.setFirstResult(Math.toIntExact(queryParameters.getOffset()));
        
        TypedQuery<Long> countQuery = em.createNamedQuery(ProjectUserEntity.COUNT_PROJECT_MEMBERS, Long.class);
        countQuery.setParameter("projectId", projectId);
        
        try {
            List<ProjectMember> members = query.getResultStream()
                .map(ProjectMapper::fromEntity)
                .collect(Collectors.toList());
            Long membersCount = countQuery.getSingleResult();
            return new EntityList<>(members, membersCount);
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public List<UserProfile> queryProjectMembers(String projectId, String query) {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setLimit(5);
        QueryUtil.overrideFilterParam(new QueryFilter("id.project.id", FilterOperation.EQ, projectId), queryParameters);
        
        var queryOpt = prepareQuery(query);
        CriteriaFilter<ProjectUserEntity> searchFilter = null;
        if (queryOpt.isPresent()) {
            String preparedQuery = queryOpt.get();
            searchFilter = (p, cb, r) -> {
                Path<UserEntity> userPath = r.get("id").get("user");
                Path<ProjectEntity> projectPath = r.get("id").get("project");
                Expression<String> usernameQuery = cb.function(
                    "REPLACE",
                    String.class,
                    cb.function(
                        "REPLACE",
                        String.class,
                        cb.function(
                            "REPLACE",
                            String.class,
                            cb.lower(userPath.get("username")),
                            cb.literal("š"),
                            cb.literal("s")
                        ),
                        cb.literal("ž"),
                        cb.literal("z")
                    ),
                    cb.literal("č"),
                    cb.literal("c")
                );
                
                Expression<String> firstNameQuery = cb.function(
                    "REPLACE",
                    String.class,
                    cb.function(
                        "REPLACE",
                        String.class,
                        cb.function(
                            "REPLACE",
                            String.class,
                            cb.lower(userPath.get("firstName")),
                            cb.literal("š"),
                            cb.literal("s")
                        ),
                        cb.literal("ž"),
                        cb.literal("z")
                    ),
                    cb.literal("č"),
                    cb.literal("c")
                );
                
                Expression<String> lastNameQuery = cb.function(
                    "REPLACE",
                    String.class,
                    cb.function(
                        "REPLACE",
                        String.class,
                        cb.function(
                            "REPLACE",
                            String.class,
                            cb.lower(userPath.get("lastName")),
                            cb.literal("š"),
                            cb.literal("s")
                        ),
                        cb.literal("ž"),
                        cb.literal("z")
                    ),
                    cb.literal("č"),
                    cb.literal("c")
                );
                
                return cb.and(
                    cb.equal(
                        cb.literal(projectId),
                        projectPath.get("id")
                    ),
                    cb.or(
                        cb.like(usernameQuery, preparedQuery),
                        cb.or(
                            cb.like(firstNameQuery, preparedQuery),
                            cb.like(lastNameQuery, preparedQuery)
                        )
                    ));
            };
        }
        
        return JPAUtils.getEntityStream(em, ProjectUserEntity.class, queryParameters, searchFilter)
            .map(ProjectUserEntity::getUser)
            .map(UserMapper::toSimpleProfile)
            .collect(Collectors.toList());
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
    public void addUserToProject(String projectId, ProjectMember member) {
        ProjectEntity project = getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        UserEntity user = userService.getUserEntityById(member.getUserId())
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        ProjectRoleEntity role = getProjectRoleEntity(member.getProjectRoleId())
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        ProjectRolesCount currentProjectRoles = getProjectRolesCount(projectId);
        if (role.getRoleId().equals(ProjectRoleEntity.PROJECT_ROLE_SCRUM_MASTER) &&
            currentProjectRoles.getScrumMastersCount() != 0) {
            throw new BadRequestException("error.project.roles.limit.scrum-master");
        }
        if (role.getRoleId().equals(ProjectRoleEntity.PROJECT_ROLE_PRODUCT_OWNER) &&
            currentProjectRoles.getProductOwnersCount() != 0) {
            throw new BadRequestException("error.project.roles.limit.product-owner");
        }
        
        ProjectUserEntity entity = new ProjectUserEntity();
        ProjectUserId entityId = new ProjectUserId();
        entityId.setUser(user);
        entityId.setProject(project);
        entity.setId(entityId);
        entity.setProjectRole(role);
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void removeUserFromProject(String projectId, String userId) {
        Query query = em.createNamedQuery(ProjectUserEntity.DELETE_BY_USER_AND_PROJECT);
        query.setParameter("projectId", projectId);
        query.setParameter("userId", userId);
        
        try {
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void updateUserProjectRole(String projectId, String userId, ProjectMember member) {
        TypedQuery<ProjectUserEntity> query = em.createNamedQuery(ProjectUserEntity.GET_BY_USER_AND_PROJECT, ProjectUserEntity.class);
        query.setParameter("projectId", projectId);
        query.setParameter("userId", userId);
        
        ProjectRoleEntity role = getProjectRoleEntity(member.getProjectRoleId())
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        ProjectRolesCount currentProjectRoles = getProjectRolesCount(projectId);
        
        if (role.getRoleId().equals(ProjectRoleEntity.PROJECT_ROLE_SCRUM_MASTER) &&
            currentProjectRoles.getScrumMastersCount() != 0) {
            throw new BadRequestException("error.project.roles.limit.scrum-master");
        }
        if (role.getRoleId().equals(ProjectRoleEntity.PROJECT_ROLE_PRODUCT_OWNER) &&
            currentProjectRoles.getProductOwnersCount() != 0) {
            throw new BadRequestException("error.project.roles.limit.product-owner");
        }
        
        try {
            em.getTransaction().begin();
            ProjectUserEntity projectUserEntity = query.getSingleResult();
            projectUserEntity.setProjectRole(role);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            throw new NotFoundException("error.not-found");
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public ProjectRolesCount getProjectRolesCount(String projectId) {
        ProjectMembersAggregated currentProjectRoles = getProjectRolesCountAggregated(projectId);
        
        ProjectRolesCount count = ProjectMapper.fromAggregated(currentProjectRoles);
        count.setProjectId(projectId);
        
        return count;
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
    
    private Optional<ProjectRoleEntity> getProjectRoleEntity(String roleId) {
        TypedQuery<ProjectRoleEntity> query = em.createNamedQuery(ProjectRoleEntity.GET_BY_ROLE_ID, ProjectRoleEntity.class);
        query.setParameter("roleId", roleId);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
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
    
    private ProjectMembersAggregated getProjectRolesCountAggregated(String projectId) {
        TypedQuery<ProjectMembersAggregated> query = em.createNamedQuery(ProjectUserEntity.GET_ROLES_COUNT, ProjectMembersAggregated.class);
        query.setParameter("projectId", projectId);
        try {
            return query.getSingleResult();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private Optional<String> prepareQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.isEmpty() || rawQuery.isBlank()) {
            return Optional.empty();
        }
        String latinizedQuery = rawQuery.trim().toLowerCase()
            .replace("č", "c")
            .replace("š", "s")
            .replace("ž", "z");
        return Optional.of("%" + latinizedQuery + "%");
    }
}
