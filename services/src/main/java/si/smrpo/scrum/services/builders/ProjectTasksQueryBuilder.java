package si.smrpo.scrum.services.builders;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.lib.stories.ExtendedTask;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.mappers.StoryMapper;
import si.smrpo.scrum.mappers.TaskMapper;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.sprint.SprintEntity;
import si.smrpo.scrum.persistence.story.TaskEntity;
import si.smrpo.scrum.persistence.story.TaskHourEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectTasksQueryBuilder {
    
    private static final Logger LOG = LogManager.getLogger(ProjectTasksQueryBuilder.class.getName());
    
    public static ProjectTasksQueryBuilder newBuilder(EntityManager em) {
        return new ProjectTasksQueryBuilder(em);
    }
    
    private final EntityManager em;
    
    private TypedQuery<TaskEntity> query;
    
    private TypedQuery<SprintEntity> activeSprintQuery;
    
    private TypedQuery<Long> countQuery;
    
    private String projectId;
    
    private ProjectTasksQueryBuilder(EntityManager em) {
        this.em = em;
    }
    
    public ProjectTasksQueryBuilder build(String projectId, QueryParameters queryParameters) {
        this.projectId = projectId;
        Date now = new Date();
    
        String activeSprintSql = "SELECT s FROM SprintEntity s " +
            "WHERE s.endDate >= :now AND s.startDate <= :now " +
            "AND s.project.id = :projectId AND s.status = 'ACTIVE'";
        activeSprintQuery = em.createQuery(activeSprintSql, SprintEntity.class);
        activeSprintQuery.setParameter("projectId", projectId);
        activeSprintQuery.setParameter("now", now);
        
        String sql = "SELECT t FROM TaskEntity t " +
            "LEFT JOIN StoryEntity s ON t.story = s " +
            "LEFT JOIN SprintStoryEntity ss ON ss.id.story = s AND ss.id.sprint.id = :sprintId " +
            "WHERE t.story.project.id = :projectId " +
            "AND t.status = 'ACTIVE' ORDER BY t.createdAt";
        query = em.createQuery(sql, TaskEntity.class);
        query.setParameter("projectId", projectId);
        
        query.setMaxResults(Math.toIntExact(queryParameters.getLimit()));
        query.setFirstResult(Math.toIntExact(queryParameters.getOffset()));
    
        String countSql = "SELECT COUNT(t) FROM TaskEntity t " +
            "LEFT JOIN StoryEntity s ON t.story = s " +
            "LEFT JOIN SprintStoryEntity ss ON ss.id.story = s AND ss.id.sprint.id = :sprintId " +
            "WHERE t.story.project.id = :projectId " +
            "AND t.status = 'ACTIVE'";
        countQuery = em.createQuery(countSql, Long.class);
        countQuery.setParameter("projectId", projectId);
        
        
        return this;
    }
    
    public EntityList<ExtendedTask> getQueryResult(String userId) {
        String sprintId = getActiveSprint().map(BaseEntity::getId)
            .orElse(null);
        query.setParameter("sprintId", sprintId);
        countQuery.setParameter("sprintId", sprintId);
    
        String activeTaskId = getUserActiveTaskEntity(projectId, userId)
            .map(taskHour -> taskHour.getTask().getId())
            .orElse(null);
        
        List<ExtendedTask> tasks = query.getResultStream()
            .map(entity -> {
                Task task = TaskMapper.fromEntity(entity);
                ExtendedTask extendedTask = new ExtendedTask(task);
                extendedTask.setActive(task.getId().equals(activeTaskId));
                if (entity.getStory() != null) {
                    extendedTask.setStory(StoryMapper.fromEntity(entity.getStory()));
                }
                return extendedTask;
            })
            .collect(Collectors.toList());
        
        Long tasksCount = countQuery.getSingleResult();
        
        return new EntityList<>(tasks, tasksCount);
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
    
    private Optional<TaskHourEntity> getUserActiveTaskEntity(String projectId, String userId) {
        TypedQuery<TaskHourEntity> query = em.createNamedQuery(TaskHourEntity.GET_ACTIVE_TASK, TaskHourEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("projectId", projectId);
        
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
