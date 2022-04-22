package si.smrpo.scrum.services.builders;

import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.params.ProjectWallPostFilters;
import si.smrpo.scrum.lib.projects.ProjectWallPost;
import si.smrpo.scrum.mappers.ProjectWallPostMapper;
import si.smrpo.scrum.persistence.aggregators.ProjectWallPostAggregated;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectWallPostQueryBuilder {
    
    
    public static ProjectWallPostQueryBuilder newBuilder(EntityManager em) {
        return new ProjectWallPostQueryBuilder(em);
    }
    
    private final EntityManager em;
    
    private TypedQuery<ProjectWallPostAggregated> query;
    
    private TypedQuery<Long> countQuery;
    
    private ProjectWallPostQueryBuilder(EntityManager em) {
        this.em = em;
    }
    
    public ProjectWallPostQueryBuilder build(String projectId, ProjectWallPostFilters filters) {
        
        String sql = "SELECT new si.smrpo.scrum.persistence.aggregators.ProjectWallPostAggregated(p, SIZE(p.comments)) " +
            "FROM ProjectWallPostEntity p " +
            // "LEFT JOIN ProjectWallCommentEntity c ON c.post = p " +
            "WHERE p.status = 'ACTIVE' AND p.project.id = :projectId";
        
        String countSql = "SELECT COUNT(p) " +
            "FROM ProjectWallPostEntity p " +
            "WHERE p.status = 'ACTIVE' AND p.project.id = :projectId";
        
        sql += " ORDER BY p.createdAt " + (filters.isSortCreatedAtDesc() ? "DESC" : "ASC");
        
        query = em.createQuery(sql, ProjectWallPostAggregated.class);
        countQuery = em.createQuery(countSql, Long.class);
        
        query.setParameter("projectId", projectId);
        countQuery.setParameter("projectId", projectId);
        
        query.setMaxResults(filters.getLimit());
        query.setFirstResult(filters.getOffset());
        
        return this;
    }
    
    public EntityList<ProjectWallPost> getQueryResult() {
        List<ProjectWallPost> posts = query.getResultStream()
            .map(entity -> {
                return ProjectWallPostMapper.fromAggregatedEntity(entity, false, true, false);
            })
            .collect(Collectors.toList());
        
        Long postCount = countQuery.getSingleResult();
        
        return new EntityList<>(posts, postCount);
    }
    
}
