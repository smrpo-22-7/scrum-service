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
import com.mjamsek.rest.exceptions.*;
import com.mjamsek.rest.services.Validator;
import com.mjamsek.rest.utils.DatetimeUtil;
import com.mjamsek.rest.utils.QueryUtil;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.requests.TaskAssignmentRequest;
import si.smrpo.scrum.lib.stories.ExtendedTask;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.lib.stories.TaskHour;
import si.smrpo.scrum.lib.stories.TaskWorkSpent;
import si.smrpo.scrum.lib.ws.SocketMessage;
import si.smrpo.scrum.mappers.TaskMapper;
import si.smrpo.scrum.persistence.story.StoryEntity;
import si.smrpo.scrum.persistence.story.TaskEntity;
import si.smrpo.scrum.persistence.story.TaskHourEntity;
import si.smrpo.scrum.persistence.story.TaskWorkSpentEntity;
import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.services.ProjectAuthorizationService;
import si.smrpo.scrum.services.SocketService;
import si.smrpo.scrum.services.StoryService;
import si.smrpo.scrum.services.TaskService;
import si.smrpo.scrum.services.builders.ProjectTasksQueryBuilder;
import si.smrpo.scrum.services.builders.StoryTasksQueryBuilder;
import si.smrpo.scrum.utils.DateUtils;
import si.smrpo.scrum.utils.NumberUtils;
import si.smrpo.scrum.utils.SetterUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestScoped
public class TaskServiceImpl implements TaskService {
    
    private static final Logger LOG = LogManager.getLogger(TaskServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private StoryService storyService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private SocketService socketService;
    
    @Inject
    private Validator validator;
    
    @Inject
    private ProjectAuthorizationService projAuth;
    
    @Inject
    private AuthContext authContext;
    
    @Override
    public List<ExtendedTask> getStoryTasks(String storyId) {
        /*TypedQuery<TaskEntity> query = em.createNamedQuery(TaskEntity.GET_BY_STORY, TaskEntity.class);
        query.setParameter("storyId", storyId);
        
        String activeTaskId = getUserStoryActiveTaskEntity(storyId)
            .map(taskHour -> taskHour.getTask().getId())
            .orElse(null);
        
        try {
            return query.getResultStream()
                .map(TaskMapper::fromEntity)
                .map(task -> {
                    ExtendedTask extendedTask = new ExtendedTask(task);
                    extendedTask.setActive(task.getId().equals(activeTaskId));
                    return extendedTask;
                })
                .collect(Collectors.toList());
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }*/
        return StoryTasksQueryBuilder.newBuilder(em)
            .build(storyId)
            .getQueryResult(authContext.getId());
    }
    
    @Override
    public EntityList<ExtendedTask> getActiveSprintTasks(String projectId, QueryParameters queryParameters) {
        return ProjectTasksQueryBuilder.newBuilder(em)
            .build(projectId, queryParameters)
            .getQueryResult(authContext.getId());
    }
    
    @Override
    public Optional<TaskEntity> getTaskEntityById(String taskId) {
        return Optional.ofNullable(em.find(TaskEntity.class, taskId));
    }
    
    @Override
    public Task createTask(String storyId, Task task) {
        validator.assertNotBlank(task.getDescription());
        
        if (task.getEstimate() != null && task.getEstimate() <= 0) {
            throw new ValidationException("error.validation");
        }
        
        StoryEntity story = storyService.getStoryEntityById(storyId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (!projAuth.isScrumMaster(story.getProject().getId(), authContext.getId()) &
            !projAuth.isProjectMember(story.getProject().getId(), authContext.getId())) {
            throw new ForbiddenException("error.forbidden");
        }
        
        TaskEntity entity = new TaskEntity();
        entity.setDescription(task.getDescription());
        entity.setStory(story);
        entity.setStatus(SimpleStatus.ACTIVE);
        if (task.getEstimate() != null) {
            entity.setEstimate(task.getEstimate());
        }
        entity.setCompleted(false);
        
        entity.setPendingAssignment(true);
        if (task.getAssignment() != null &&
            task.getAssignment().getAssigneeId() != null &&
            !task.getAssignment().getAssigneeId().isBlank()) {
            
            projAuth.isInProjectOrThrow(story.getProject().getId(), task.getAssignment().getAssigneeId());
            
            UserEntity user = userService.getUserEntityById(task.getAssignment().getAssigneeId())
                .orElseThrow(() -> new NotFoundException("error.not-found"));
            entity.setAssignee(user);
        }
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return TaskMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Task updateTask(String taskId, Task task) {
        
        if (task.getEstimate() != null) {
            if (task.getEstimate() <= 0) {
                throw new ValidationException("error.validation");
            }
        }
        if (task.getDescription() != null) {
            if (task.getDescription().trim().length() == 0) {
                throw new ValidationException("error.validation");
            }
        }
        
        TaskEntity entity = getTaskEntityById(taskId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (!projAuth.isScrumMaster(entity.getStory().getProject().getId(), authContext.getId()) &
            !projAuth.isProjectMember(entity.getStory().getProject().getId(), authContext.getId())) {
            throw new ForbiddenException("error.forbidden");
        }
        
        UserEntity user = null;
        if (task.getAssignment() != null) {
            if (task.getAssignment().getAssigneeId() != null) {
                boolean isInProject = projAuth.isInProject(entity.getStory().getProject().getId(), task.getAssignment().getAssigneeId());
                if (!isInProject) {
                    throw new ValidationException("error.validation");
                }
                user = userService.getUserEntityById(task.getAssignment().getAssigneeId())
                    .orElseThrow(() -> new ValidationException("error.validation"));
            }
        }
        
        try {
            em.getTransaction().begin();
            SetterUtil.setIfNotNull(task.getDescription(), entity::setDescription);
            SetterUtil.setIfNotNull(task.getEstimate(), entity::setEstimate);
            SetterUtil.setIfNotNull(task.getCompleted(), entity::setCompleted);
            
            if (task.getAssignment() != null) {
                if (task.getAssignment().getAssigneeId() != null) {
                    entity.setAssignee(user);
                    // if self-assigned, then no pending required
                    entity.setPendingAssignment(!authContext.getId().equals(task.getAssignment().getAssigneeId()));
                }
            }
            
            em.getTransaction().commit();
            return TaskMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void removeTask(String taskId) {
        getTaskEntityById(taskId).ifPresent(entity -> {
            if (!projAuth.isScrumMaster(entity.getStory().getProject().getId(), authContext.getId()) &
                !projAuth.isProjectMember(entity.getStory().getProject().getId(), authContext.getId())) {
                throw new ForbiddenException("error.forbidden");
            }
            
            try {
                em.getTransaction().begin();
                entity.setStatus(SimpleStatus.DISABLED);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                LOG.error(e);
                em.getTransaction().rollback();
                throw new RestException("error.server");
            }
        });
    }
    
    @Override
    public void requestTaskForUser(String taskId, TaskAssignmentRequest request) {
        validator.assertNotBlank(request.getUserId());
        
        UserEntity user = userService.getUserEntityById(request.getUserId())
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        getTaskEntityById(taskId).ifPresent(entity -> {
            if (!projAuth.isScrumMaster(entity.getStory().getProject().getId(), authContext.getId()) &
                !projAuth.isProjectMember(entity.getStory().getProject().getId(), authContext.getId())) {
                throw new ForbiddenException("error.forbidden");
            }
            
            try {
                em.getTransaction().begin();
                entity.setAssignee(user);
                entity.setPendingAssignment(true);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                LOG.error(e);
                em.getTransaction().rollback();
                throw new RestException("error.server");
            }
        });
    }
    
    @Override
    public void acceptTaskRequest(String taskId) {
        TaskEntity task = getTaskEntityById(taskId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (task.getAssignee() == null || !task.getAssignee().getId().equals(authContext.getId())) {
            throw new ForbiddenException("error.forbidden");
        }
        
        try {
            em.getTransaction().begin();
            task.setPendingAssignment(false);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void rejectTaskRequest(String taskId) {
        TaskEntity task = getTaskEntityById(taskId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (task.getAssignee() == null || !task.getAssignee().getId().equals(authContext.getId())) {
            throw new ForbiddenException("error.forbidden");
        }
        
        try {
            em.getTransaction().begin();
            task.setPendingAssignment(true);
            task.setAssignee(null);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void clearAssignee(String taskId) {
        TaskEntity task = getTaskEntityById(taskId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (!projAuth.isScrumMaster(task.getStory().getProject().getId(), authContext.getId()) &
            !projAuth.isProjectMember(task.getStory().getProject().getId(), authContext.getId())) {
            throw new ForbiddenException("error.forbidden");
        }
        
        try {
            em.getTransaction().begin();
            task.setPendingAssignment(true);
            task.setAssignee(null);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void startWorkOnTask(String taskId) {
        TaskEntity task = getTaskEntityById(taskId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        UserEntity user = userService.getUserEntityById(authContext.getId())
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        TaskHourEntity taskEntity = new TaskHourEntity();
        taskEntity.setTask(task);
        taskEntity.setStartDate(new Date());
        taskEntity.setUser(user);
        
        try {
            em.getTransaction().begin();
            em.persist(taskEntity);
            em.getTransaction().commit();
            
            SocketMessage message = new SocketMessage();
            message.setType("TIMER_START");
            message.setPayload(taskEntity.getStartDate().toInstant().toString());
            socketService.sendMessage(message, authContext.getId());
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void endWorkOnTask(String projectId) {
        TaskHourEntity task = getUserActiveTaskEntity(projectId)
            .orElseThrow(() -> new BadRequestException("error.bad-request"));
        
        try {
            em.getTransaction().begin();
            Date now = new Date();
            task.setEndDate(now);
            double quarterlyDiff = DateUtils.getQuarterHourDiff(now, task.getStartDate());
            double quarterlyAmount = Math.max(quarterlyDiff, 0.25);
            task.setAmount(quarterlyAmount);
            
            getTaskWork(task.getTask().getId())
                .ifPresentOrElse(prevWork -> {
                    prevWork.setAmount(prevWork.getAmount() + quarterlyAmount);
                    prevWork.setRemainingAmount(Math.max(prevWork.getRemainingAmount() - quarterlyAmount, 0));
                }, () -> {
                    TaskWorkSpentEntity prevWork = new TaskWorkSpentEntity();
                    prevWork.setAmount(quarterlyAmount);
                    double timeEstimate = Math.max(NumberUtils.roundToQuarter(task.getTask().getEstimate()), 0.25);
                    double remainingAmount = Math.max(timeEstimate - quarterlyAmount, 0);
                    prevWork.setRemainingAmount(remainingAmount);
                    prevWork.setWorkDate(task.getStartDate());
                    prevWork.setTask(task.getTask());
                    prevWork.setUser(task.getUser());
                    em.persist(prevWork);
                });
            
            em.getTransaction().commit();
            
            SocketMessage message = new SocketMessage();
            message.setType("TIMER_END");
            socketService.sendMessage(message, authContext.getId());
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public TaskHour getUserActiveTask(String projectId) {
        return getUserActiveTaskEntity(projectId)
            .map(TaskMapper::fromEntity)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
    }
    
    @Override
    public TaskWorkSpent updateTaskHours(String hourId, TaskWorkSpent taskWork) {
        TaskWorkSpentEntity workEntity = getTaskWorkEntity(hourId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        try {
            em.getTransaction().begin();
            if (taskWork.getAmount() != null) {
                double amount = Math.max(NumberUtils.roundToQuarter(taskWork.getAmount()), 0.25);
                workEntity.setAmount(amount);
            }
            if (taskWork.getRemainingAmount() != null) {
                double remainingAmount = Math.max(NumberUtils.roundToQuarter(taskWork.getRemainingAmount()), 0);
                workEntity.setRemainingAmount(remainingAmount);
            }
            em.getTransaction().commit();
            return TaskMapper.fromEntity(workEntity);
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void updateTaskHoursByDate(String taskId, TaskWorkSpent taskWork) {
        
        try {
            em.getTransaction().begin();
    
            getTaskWorkByDate(taskId, Date.from(taskWork.getWorkDate()))
                .ifPresentOrElse(savedHours -> {
                    SetterUtil.setIfNotNull(taskWork.getAmount(), savedHours::setAmount);
                    SetterUtil.setIfNotNull(taskWork.getRemainingAmount(), savedHours::setRemainingAmount);
                }, () -> {
                    TaskEntity task = getTaskEntityById(taskId)
                        .orElseThrow(() -> new NotFoundException("error.not-found"));
    
                    TaskWorkSpentEntity prevWork = new TaskWorkSpentEntity();
                    double quarterlyAmount = 0.0;
                    if (taskWork.getAmount() != null) {
                        quarterlyAmount = Math.max(NumberUtils.roundToQuarter(taskWork.getAmount()), 0.25);
                        prevWork.setAmount(quarterlyAmount);
                    } else {
                        prevWork.setAmount(0.0);
                    }
                    if (taskWork.getRemainingAmount() != null) {
                        prevWork.setRemainingAmount(taskWork.getRemainingAmount());
                    } else if (prevWork.getRemainingAmount() == null) {
                        double timeEstimate = Math.max(NumberUtils.roundToQuarter(task.getEstimate()), 0.25);
                        double remainingAmount = Math.max(timeEstimate - quarterlyAmount, 0);
                        prevWork.setRemainingAmount(remainingAmount);
                    }
                    
                    prevWork.setWorkDate(Date.from(DateUtils.truncateTime(taskWork.getWorkDate())));
                    prevWork.setTask(task);
                    prevWork.setUser(task.getAssignee());
                    em.persist(prevWork);
                });
            
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public List<TaskWorkSpent> getTaskHours(String taskId) {
        QueryParameters q = new QueryParameters();
        QueryUtil.overrideFilterParam(new QueryFilter("task.id", FilterOperation.EQ, taskId), q);
        QueryUtil.overrideFilterParam(new QueryFilter("user.id", FilterOperation.EQ, authContext.getId()), q);
        return JPAUtils.getEntityStream(em, TaskWorkSpentEntity.class, q)
            .map(TaskMapper::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Override
    public void removeTaskHours(String hourId) {
        getTaskWorkEntity(hourId).ifPresent(entity -> {
            try {
                em.getTransaction().begin();
                em.remove(entity);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("error.server");
            }
        });
    }
    
    @Override
    public Optional<TaskHourEntity> getUserActiveTaskEntity(String projectId) {
        TypedQuery<TaskHourEntity> query = em.createNamedQuery(TaskHourEntity.GET_ACTIVE_TASK, TaskHourEntity.class);
        query.setParameter("userId", authContext.getId());
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
    
    private Optional<TaskHourEntity> getUserStoryActiveTaskEntity(String storyId) {
        TypedQuery<TaskHourEntity> query = em.createNamedQuery(TaskHourEntity.GET_ACTIVE_TASK_BY_STORY, TaskHourEntity.class);
        query.setParameter("userId", authContext.getId());
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
    
    @Override
    public EntityList<TaskWorkSpent> getUserTaskWorkSpent(String projectId, String userId, QueryParameters queryParameters) {
        QueryUtil.setDefaultOrderParam(new QueryOrder("workDate", OrderDirection.DESC), queryParameters);
        QueryUtil.overrideFilterParam(new QueryFilter("task.story.project.id", FilterOperation.EQ, projectId), queryParameters);
        QueryUtil.overrideFilterParam(new QueryFilter("user.id", FilterOperation.EQ, authContext.getId()), queryParameters);
        
        List<TaskWorkSpent> hours = JPAUtils.getEntityStream(em, TaskWorkSpentEntity.class, queryParameters)
            .map(TaskMapper::fromEntity)
            .collect(Collectors.toList());
        
        long hoursCount = JPAUtils.queryEntitiesCount(em, TaskWorkSpentEntity.class, queryParameters);
        
        return new EntityList<>(hours, hoursCount);
    }
    
    @Override
    public EntityList<TaskWorkSpent> getCurrentUserTaskWorkSpent(String projectId, QueryParameters queryParameters) {
        return getUserTaskWorkSpent(projectId, authContext.getId(), queryParameters);
    }
    
    private Optional<TaskWorkSpentEntity> getTaskWork(String taskId) {
        TypedQuery<TaskWorkSpentEntity> query = em.createNamedQuery(TaskWorkSpentEntity.GET_BY_TASK_ID, TaskWorkSpentEntity.class);
        query.setParameter("userId", authContext.getId());
        query.setParameter("taskId", taskId);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private Optional<TaskWorkSpentEntity> getTaskWorkByDate(String taskId, Date date) {
        TypedQuery<TaskWorkSpentEntity> query = em.createNamedQuery(TaskWorkSpentEntity.GET_BY_DATE_AND_TASK_ID, TaskWorkSpentEntity.class);
        query.setParameter("userId", authContext.getId());
        query.setParameter("taskId", taskId);
        query.setParameter("date", DateUtils.truncateTime(date));
    
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private Optional<TaskWorkSpentEntity> getTaskWorkEntity(String hourId) {
        return Optional.ofNullable(em.find(TaskWorkSpentEntity.class, hourId));
    }
}
