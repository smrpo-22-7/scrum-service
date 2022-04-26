package si.smrpo.scrum.services.builders;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.lib.enums.StoryStatus;
import si.smrpo.scrum.lib.params.ProjectStoriesFilters;
import si.smrpo.scrum.lib.responses.ExtendedStory;
import si.smrpo.scrum.mappers.StoryMapper;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.aggregators.ExtendedStoryAggregated;
import si.smrpo.scrum.persistence.sprint.SprintEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectStoryQueryBuilder {
    
    private static final Logger LOG = LogManager.getLogger(ProjectStoryQueryBuilder.class.getName());
    
    public static ProjectStoryQueryBuilder newBuilder(EntityManager em) {
        return new ProjectStoryQueryBuilder(em);
    }
    
    
    private final EntityManager em;
    
    private TypedQuery<ExtendedStoryAggregated> query;
    
    private TypedQuery<SprintEntity> activeSprintQuery;
    
    private TypedQuery<Long> countQuery;
    
    private ProjectStoryQueryBuilder(EntityManager em) {
        this.em = em;
    }
    
    public ProjectStoryQueryBuilder build(String projectId, ProjectStoriesFilters filters) {
        Date now = new Date();
        
        String activeSprintSql = "SELECT s FROM SprintEntity s " +
            "WHERE s.endDate >= :now AND s.startDate <= :now " +
            "AND s.project.id = :projectId AND s.status = 'ACTIVE'";
        activeSprintQuery = em.createQuery(activeSprintSql, SprintEntity.class);
        activeSprintQuery.setParameter("projectId", projectId);
        activeSprintQuery.setParameter("now", now);
        
        String sql = "SELECT new si.smrpo.scrum.persistence.aggregators.ExtendedStoryAggregated(" +
            "s, ss.id IS NOT NULL, ss.id.sprint.id, " +
            "(SELECT COUNT(t) FROM TaskEntity t WHERE t.story = s), " +
            "(SELECT COUNT(t) FROM TaskEntity t WHERE t.story = s AND t.completed = true) " +
            ") " +
            "FROM StoryEntity s " +
            "LEFT JOIN SprintStoryEntity ss ON ss.id.story = s AND ss.id.sprint.id = :sprintId " +
            "WHERE s.project.id = :projectId AND s.status = 'ACTIVE'";
        
        String countSql = "SELECT COUNT(s) FROM StoryEntity s " +
            "LEFT JOIN SprintStoryEntity ss ON ss.id.story = s AND ss.id.sprint.id = :sprintId " +
            "WHERE s.project.id = :projectId AND s.status = 'ACTIVE'";
        
        if (filters.getFilterRealized() != null) {
            sql += " AND s.storyStatus IN :storyStatuses";
            countSql += " AND s.storyStatus IN :storyStatuses";
        }
        if (filters.getFilterAssigned() != null) {
            sql += " AND (CASE WHEN (ss.id IS NOT NULL) THEN true ELSE false END) = :activeSprintOnly";
            countSql += " AND (CASE WHEN (ss.id IS NOT NULL) THEN true ELSE false END) = :activeSprintOnly";
        }
        
        sql += " ORDER BY s.numberId " + (filters.getNumberIdSortAsc() ? "ASC" : "DESC");
        
        query = em.createQuery(sql, ExtendedStoryAggregated.class);
        countQuery = em.createQuery(countSql, Long.class);
        
        query.setParameter("projectId", projectId);
        countQuery.setParameter("projectId", projectId);
        
        if (filters.getFilterRealized() != null) {
            List<StoryStatus> filterState = filters.getFilterRealized() ?
                List.of(StoryStatus.REALIZED) :
                List.of(StoryStatus.REJECTED, StoryStatus.WAITING);
            query.setParameter("storyStatuses", filterState);
            countQuery.setParameter("storyStatuses", filterState);
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
        String sprintId = getActiveSprint().map(BaseEntity::getId)
            .orElse(null);
        
        query.setParameter("sprintId", sprintId);
        countQuery.setParameter("sprintId", sprintId);
        
        List<ExtendedStory> stories = query.getResultStream()
            .distinct()
            .map(entity -> {
                ExtendedStory story = new ExtendedStory(StoryMapper.fromEntity(entity.getStory()));
                story.setAssignedSprintId(entity.getAssignedTo());
                story.setInActiveSprint(entity.isAssigned());
                story.setCompleted(entity.getTotalTasks() <= 0 || entity.getCompletedTasks().equals(entity.getTotalTasks()));
                return story;
            })
            .collect(Collectors.toList());
        
        Long storyCount = countQuery.getSingleResult();
        
        return new EntityList<>(stories, storyCount);
    }
    
    private Optional<SprintEntity> getActiveSprint() {
        try {
            return Optional.of(activeSprintQuery.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
}
