package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.integrations.auth.mappers.RoleMapper;
import si.smrpo.scrum.integrations.auth.services.RoleService;
import si.smrpo.scrum.lib.SysRole;
import si.smrpo.scrum.persistence.users.SysRoleEntity;
import si.smrpo.scrum.persistence.users.UserSysRolesEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestScoped
public class RoleServiceImpl implements RoleService {
    
    private static final Logger LOG = LogManager.getLogger(RoleServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Override
    public Set<String> getUserRoles(String userId) {
        return getUserRoleEntities(userId).stream()
            .map(SysRoleEntity::getRoleId)
            .collect(Collectors.toSet());
    }
    
    @Override
    public Set<SysRoleEntity> getUserRoleEntities(String userId) {
        TypedQuery<SysRoleEntity> query = em.createNamedQuery(UserSysRolesEntity.GET_USER_ROLES, SysRoleEntity.class);
        query.setParameter("userId", userId);
        try {
            return new HashSet<>(query.getResultList());
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Set<SysRoleEntity> getSysRoles(Set<String> roles) {
        TypedQuery<SysRoleEntity> query = em.createNamedQuery(SysRoleEntity.GET_BY_ROLE_IDS, SysRoleEntity.class);
        query.setParameter("roleIds", roles);
        return query.getResultStream().collect(Collectors.toSet());
    }
    
    @Override
    public Set<SysRoleEntity> getAllSysRoleEntities() {
        return JPAUtils.getEntityStream(em, SysRoleEntity.class, new QueryParameters())
            .collect(Collectors.toSet());
    }
    
    @Override
    public Set<SysRole> getAllSysRoles() {
        return getAllSysRoleEntities().stream()
            .map(RoleMapper::fromEntity)
            .collect(Collectors.toSet());
    }
    
    @Override
    public Optional<SysRoleEntity> getSysRoleEntity(String roleId) {
        TypedQuery<SysRoleEntity> query = em.createNamedQuery(SysRoleEntity.GET_BY_ROLE_ID, SysRoleEntity.class);
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
}
