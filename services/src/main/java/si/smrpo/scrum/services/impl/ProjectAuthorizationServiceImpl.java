package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.ForbiddenException;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.persistence.project.ProjectRoleEntity;
import si.smrpo.scrum.persistence.project.ProjectUserEntity;
import si.smrpo.scrum.services.ProjectAuthorizationService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@RequestScoped
public class ProjectAuthorizationServiceImpl implements ProjectAuthorizationService {
    
    private static final Logger LOG = LogManager.getLogger(ProjectAuthorizationServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Override
    public boolean isInProject(String projectId, String userId) {
        return getProjectMembership(projectId, userId).isPresent();
    }
    
    @Override
    public void isInProjectOrThrow(String projectId, String userId) {
        if (!isInProject(projectId, userId)) {
            throw new ForbiddenException("error.forbidden");
        }
    }
    
    @Override
    public boolean isProjectMember(String projectId, String userId) {
        return getProjectMembership(projectId, userId)
            .map(membership -> hasRole(membership, ProjectRoleEntity.PROJECT_ROLE_MEMBER))
            .orElse(false);
    }
    
    @Override
    public void isProjectMemberOrThrow(String projectId, String userId) throws ForbiddenException {
        if (!isProjectMember(projectId, userId)) {
            throw new ForbiddenException("error.forbidden");
        }
    }
    
    @Override
    public boolean isProductOwner(String projectId, String userId) {
        return getProjectMembership(projectId, userId)
            .map(membership -> hasRole(membership, ProjectRoleEntity.PROJECT_ROLE_PRODUCT_OWNER))
            .orElse(false);
    }
    
    @Override
    public void isProductOwnerOrThrow(String projectId, String userId) throws ForbiddenException {
        if (!isProductOwner(projectId, userId)) {
            throw new ForbiddenException("error.forbidden");
        }
    }
    
    @Override
    public boolean isScrumMaster(String projectId, String userId) {
        return getProjectMembership(projectId, userId)
            .map(membership -> hasRole(membership, ProjectRoleEntity.PROJECT_ROLE_SCRUM_MASTER))
            .orElse(false);
    }
    
    @Override
    public void isScrumMasterOrThrow(String projectId, String userId) throws ForbiddenException {
        if (!isScrumMaster(projectId, userId)) {
            throw new ForbiddenException("error.forbidden");
        }
    }
    
    @Override
    public boolean isProjectAdmin(String projectId, String userId) {
        return getProjectMembership(projectId, userId)
            .map(membership -> hasRole(membership, ProjectRoleEntity.PROJECT_ROLE_PRODUCT_OWNER) ||
                hasRole(membership, ProjectRoleEntity.PROJECT_ROLE_SCRUM_MASTER))
            .orElse(false);
    }
    
    @Override
    public void isProjectAdminOrThrow(String projectId, String userId) throws ForbiddenException {
        if (!isInProject(projectId, userId)) {
            throw new ForbiddenException("error.forbidden");
        }
    }
    
    private boolean hasRole(ProjectUserEntity membership, String roleId) {
        return membership.getProjectRole().getRoleId().equals(roleId);
    }
    
    private Optional<ProjectUserEntity> getProjectMembership(String projectId, String userId) {
        TypedQuery<ProjectUserEntity> query = em.createNamedQuery(ProjectUserEntity.GET_BY_USER_AND_PROJECT, ProjectUserEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("projectId", projectId);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NotFoundException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
}
