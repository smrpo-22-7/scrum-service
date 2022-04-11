package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.ForbiddenException;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.ValidationException;
import com.mjamsek.rest.services.Validator;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.requests.TaskAssignmentRequest;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.mappers.TaskMapper;
import si.smrpo.scrum.persistence.story.StoryEntity;
import si.smrpo.scrum.persistence.story.TaskEntity;
import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.services.ProjectAuthorizationService;
import si.smrpo.scrum.services.StoryService;
import si.smrpo.scrum.services.TaskService;
import si.smrpo.scrum.utils.SetterUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
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
    private Validator validator;
    
    @Inject
    private ProjectAuthorizationService projAuth;
    
    @Inject
    private AuthContext authContext;
    
    @Override
    public List<Task> getStoryTasks(String storyId) {
        TypedQuery<TaskEntity> query = em.createNamedQuery(TaskEntity.GET_BY_STORY, TaskEntity.class);
        query.setParameter("storyId", storyId);
        
        try {
            return query.getResultStream()
                .map(TaskMapper::fromEntity)
                .collect(Collectors.toList());
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<TaskEntity> getTaskEntityById(String taskId) {
        return Optional.ofNullable(em.find(TaskEntity.class, taskId));
    }
    
    @Override
    public Task createTask(String storyId, Task task) {
        validator.assertNotBlank(task.getDescription());
        if (task.getEstimate() <= 0) {
            throw new ValidationException("error.validation");
        }
        
        StoryEntity story = storyService.getStoryEntityById(storyId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (!projAuth.isProjectAdmin(story.getProject().getId(), authContext.getId()) &
            !projAuth.isProjectMember(story.getProject().getId(), authContext.getId())) {
            throw new ForbiddenException("error.forbidden");
        }
        
        TaskEntity entity = new TaskEntity();
        entity.setDescription(task.getDescription());
        entity.setStory(story);
        entity.setStatus(SimpleStatus.ACTIVE);
        entity.setEstimate(task.getEstimate());
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
        
        try {
            em.getTransaction().begin();
            SetterUtil.setIfNotNull(task.getDescription(), entity::setDescription);
            SetterUtil.setIfNotNull(task.getEstimate(), entity::setEstimate);
            SetterUtil.setIfNotNull(task.isCompleted(), entity::setCompleted);
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
}
