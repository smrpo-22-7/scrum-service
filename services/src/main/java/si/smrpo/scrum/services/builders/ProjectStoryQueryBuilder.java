package si.smrpo.scrum.services.builders;

import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.params.ProjectStoriesFilters;
import si.smrpo.scrum.lib.responses.ExtendedStory;
import si.smrpo.scrum.mappers.StoryMapper;
import si.smrpo.scrum.persistence.aggregators.ExtendedStoryAggregated;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectStoryQueryBuilder {
    
    public static ProjectStoryQueryBuilder newBuilder(EntityManager em) {
        return new ProjectStoryQueryBuilder(em);
    }
    
    private final EntityManager em;
    
    private TypedQuery<ExtendedStoryAggregated> query;
    
    private TypedQuery<Long> countQuery;
    
    private ProjectStoryQueryBuilder(EntityManager em) {
        this.em = em;
    }
    
    public ProjectStoryQueryBuilder build(String projectId, ProjectStoriesFilters filters) {
        String sql = "SELECT new si.smrpo.scrum.persistence.aggregators.ExtendedStoryAggregated(s, ss.id.sprint IS NOT NULL, ss.id.sprint.id) " +
            "FROM StoryEntity s " +
            "LEFT JOIN SprintStoryEntity ss ON ss.id.story = s " +
            "LEFT JOIN SprintEntity sp ON ss.id.sprint = sp AND sp.endDate >= :now AND sp.startDate <= :now " +
            "WHERE s.project.id = :projectId";
        String countSql = "SELECT COUNT(s) FROM StoryEntity s " +
            "LEFT JOIN SprintStoryEntity ss ON ss.id.story = s " +
            "LEFT JOIN SprintEntity sp ON ss.id.sprint = sp AND sp.endDate >= :now AND sp.startDate <= :now " +
            "WHERE s.project.id = :projectId";
        
        if (filters.getFilterRealized() != null) {
            sql += " AND s.realized = :realized";
            countSql += " AND s.realized = :realized";
        }
        if (filters.getFilterAssigned() != null) {
            sql += " AND (CASE WHEN (ss.id.sprint IS NOT NULL) THEN true ELSE false END) = :activeSprintOnly";
            countSql += " AND (CASE WHEN (ss.id.sprint IS NOT NULL) THEN true ELSE false END) = :activeSprintOnly";
        }
        
        sql += " ORDER BY s.numberId " + (filters.getNumberIdSortAsc() ? "ASC" : "DESC");
        
        query = em.createQuery(sql, ExtendedStoryAggregated.class);
        countQuery = em.createQuery(countSql, Long.class);
        
        Date now = new Date();
        query.setParameter("projectId", projectId);
        query.setParameter("now", now);
        countQuery.setParameter("projectId", projectId);
        countQuery.setParameter("now", now);
        
        if (filters.getFilterRealized() != null) {
            query.setParameter("realized", filters.getFilterRealized());
            countQuery.setParameter("realized", filters.getFilterRealized());
        }
        if (filters.getFilterAssigned() != null) {
            query.setParameter("activeSprintOnly", filters.getFilterAssigned());
            countQuery.setParameter("activeSprintOnly", filters.getFilterAssigned());
        }
        
        query.setMaxResults(filters.getLimit());
        query.setFirstResult(filters.getOffset());
        
        return this;
    }
    
    public EntityList<ExtendedStory> getQueryResult() {
        List<ExtendedStory> stories = query.getResultStream()
            .map(entity -> {
                ExtendedStory story = new ExtendedStory(StoryMapper.fromEntity(entity.getStory()));
                story.setAssignedSprintId(entity.getAssignedTo());
                story.setInActiveSprint(entity.isAssigned());
                return story;
            })
            .collect(Collectors.toList());
        
        Long storyCount = countQuery.getSingleResult();
        
        return new EntityList<>(stories, storyCount);
    }
    
}
