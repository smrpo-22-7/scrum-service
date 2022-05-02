package si.smrpo.scrum.services.builders;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.stories.ExtendedTask;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.mappers.StoryMapper;
import si.smrpo.scrum.mappers.TaskMapper;
import si.smrpo.scrum.persistence.aggregators.TaskWorkAggregated;
import si.smrpo.scrum.persistence.story.TaskEntity;
import si.smrpo.scrum.persistence.story.TaskHourEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StoryTasksQueryBuilder {
    
    private static final Logger LOG = LogManager.getLogger(StoryTasksQueryBuilder.class.getName());
    
    public static StoryTasksQueryBuilder newBuilder(EntityManager em) {
        return new StoryTasksQueryBuilder(em);
    }
    
    private final EntityManager em;
    
    private TypedQuery<TaskEntity> query;
    
    private TypedQuery<TaskWorkAggregated> taskWorkQuery;
    
    private String storyId;
    
    private StoryTasksQueryBuilder(EntityManager em) {
        this.em = em;
    }
    
    public StoryTasksQueryBuilder build(String storyId) {
        this.storyId = storyId;
        
        String sql = "SELECT t FROM TaskEntity t " +
            "WHERE t.story.id = :storyId " +
            "AND t.status = 'ACTIVE' ORDER BY t.createdAt";
        query = em.createQuery(sql, TaskEntity.class);
        query.setParameter("storyId", storyId);
        
        String taskWorkSql = "SELECT new si.smrpo.scrum.persistence.aggregators.TaskWorkAggregated(" +
            "t.task.id, COALESCE(SUM(t.amount), 0.0), COALESCE(SUM(t.remainingAmount), 0.0)" +
            ") " +
            "FROM TaskWorkSpentEntity t " +
            "WHERE t.task.id IN :taskIds " +
            "GROUP BY t.task.id";
        taskWorkQuery = em.createQuery(taskWorkSql, TaskWorkAggregated.class);
        
        return this;
    }
    
    public List<ExtendedTask> getQueryResult(String userId) {
        String activeTaskId = getUserStoryActiveTaskEntity(storyId, userId)
            .map(taskHour -> taskHour.getTask().getId())
            .orElse(null);
        
        List<ExtendedTask> tasks = query.getResultStream()
            .map(entity -> {
                Task task = TaskMapper.fromEntity(entity);
                ExtendedTask extendedTask = new ExtendedTask(task);
                extendedTask.setActive(task.getId().equals(activeTaskId));
                if (entity.getStory() != null) {
                    extendedTask.setStory(StoryMapper.toStorySimple(entity.getStory()));
                }
                return extendedTask;
            })
            .collect(Collectors.toList());
        
        List<String> taskIds = tasks.stream().map(BaseType::getId).collect(Collectors.toList());
        taskWorkQuery.setParameter("taskIds", taskIds);
        Map<String, TaskWorkAggregated> taskWorkedAmounts = taskWorkQuery.getResultStream()
            .collect(Collectors.toMap(TaskWorkAggregated::getTaskId, t -> t));
        tasks.forEach(task -> {
            if (taskWorkedAmounts.containsKey(task.getId())) {
                var aggregated = taskWorkedAmounts.get(task.getId());
                task.setAmountWorked(aggregated.getHoursSum());
                task.setAmountRemaining(aggregated.getRemainingHoursSum());
            } else {
                task.setAmountWorked(0.0);
                task.setAmountRemaining(task.getEstimate());
            }
        });
        
        return tasks;
    }
    
    private Optional<TaskHourEntity> getUserStoryActiveTaskEntity(String storyId, String userId) {
        TypedQuery<TaskHourEntity> query = em.createNamedQuery(TaskHourEntity.GET_ACTIVE_TASK_BY_STORY, TaskHourEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("storyId", storyId);
        
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
