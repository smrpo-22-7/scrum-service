package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterOperation;
import com.kumuluz.ee.rest.interfaces.CriteriaFilter;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.BadRequestException;
import com.mjamsek.rest.exceptions.ForbiddenException;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.utils.QueryUtil;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.mappers.UserMapper;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.responses.ProjectRolesCount;
import si.smrpo.scrum.mappers.ProjectMapper;
import si.smrpo.scrum.persistence.aggregators.ProjectMembersAggregated;
import si.smrpo.scrum.persistence.identifiers.ProjectUserId;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.project.ProjectRoleEntity;
import si.smrpo.scrum.persistence.project.ProjectUserEntity;
import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.services.ProjectAuthorizationService;
import si.smrpo.scrum.services.ProjectMembershipService;
import si.smrpo.scrum.services.ProjectService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestScoped
public class ProjectMembershipServiceImpl implements ProjectMembershipService {
    
    private static final Logger LOG = LogManager.getLogger(ProjectMembershipServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private ProjectAuthorizationService projAuth;
    
    @Inject
    private ProjectService projectService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private AuthContext authContext;
    
    @Override
    public List<ProjectUserEntity> getProjectMembershipEntities(String projectId) {
        TypedQuery<ProjectUserEntity> query = em.createNamedQuery(ProjectUserEntity.GET_PROJECT_MEMBERS, ProjectUserEntity.class);
        query.setParameter("projectId", projectId);
    
        try {
            return query.getResultList();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public EntityList<ProjectMember> getProjectMembers(String projectId, QueryParameters queryParameters) {
        
        if (!authContext.getSysRoles().contains(Roles.ADMIN_ROLE)) {
            projAuth.isInProjectOrThrow(projectId, authContext.getId());
        }
        
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
        projAuth.isInProjectOrThrow(projectId, authContext.getId());
        
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
    public void addUserToProject(String projectId, ProjectMember member) {
        isScrumMasterOrAdminOrThrow(projectId);
        
        ProjectEntity project = projectService.getProjectEntityById(projectId)
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
        isScrumMasterOrAdminOrThrow(projectId);
        
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
        isScrumMasterOrAdminOrThrow(projectId);
        
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
    
    private void isScrumMasterOrAdminOrThrow(String projectId) throws ForbiddenException {
        boolean isAdmin = authContext.getSysRoles().contains(Roles.ADMIN_ROLE);
        if (!isAdmin) {
            boolean isScrumMaster = projAuth.isScrumMaster(projectId, authContext.getId());
            if (!isScrumMaster) {
                throw new ForbiddenException("error.forbidden");
            }
        }
    }
}
