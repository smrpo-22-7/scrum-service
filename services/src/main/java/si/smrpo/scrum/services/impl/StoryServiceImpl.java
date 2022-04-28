package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterOperation;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.BadRequestException;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.ValidationException;
import com.mjamsek.rest.services.Validator;
import com.mjamsek.rest.utils.QueryUtil;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.StoryStatus;
import si.smrpo.scrum.lib.params.ProjectStoriesFilters;
import si.smrpo.scrum.lib.requests.ConflictCheckRequest;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.responses.ExtendedStory;
import si.smrpo.scrum.lib.stories.AcceptanceTest;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.StoryState;
import si.smrpo.scrum.mappers.StoryMapper;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.sprint.SprintStoryEntity;
import si.smrpo.scrum.persistence.story.AcceptanceTestEntity;
import si.smrpo.scrum.persistence.story.StoryEntity;
import si.smrpo.scrum.services.ProjectAuthorizationService;
import si.smrpo.scrum.services.ProjectService;
import si.smrpo.scrum.services.StoryService;
import si.smrpo.scrum.services.builders.ProjectStoryQueryBuilder;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class StoryServiceImpl implements StoryService {
    
    private static final Logger LOG = LogManager.getLogger(SprintServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private ProjectService projectService;
    
    @Inject
    private ProjectAuthorizationService projectAuthorizationService;
    
    @Inject
    private Validator validator;
    
    @Inject
    private AuthContext authContext;
    
    @Override
    public EntityList<Story> getStories(String projectId, QueryParameters queryParameters) {
        // Get all stories that are assigned to active sprint
        QueryParameters sprintParams = new QueryParameters();
        Date now = new Date();
        QueryUtil.overrideFilterParam(new QueryFilter("id.story.project.id", FilterOperation.EQ, projectId), sprintParams);
        QueryUtil.overrideFilterParam(new QueryFilter("id.sprint.startDate", FilterOperation.LTE, now), sprintParams);
        QueryUtil.overrideFilterParam(new QueryFilter("id.sprint.endDate", FilterOperation.GT, now), sprintParams);
        Map<String, SprintStoryEntity> sprintStories = JPAUtils.getEntityStream(em, SprintStoryEntity.class, sprintParams)
            .collect(Collectors.toMap(s -> s.getStory().getId(), s -> s));
        
        // Get all project stories
        QueryUtil.overrideFilterParam(new QueryFilter("project.id", FilterOperation.EQ, projectId), queryParameters);
        List<Story> stories = JPAUtils.getEntityStream(em, StoryEntity.class, queryParameters)
            .map(StoryMapper::fromEntity)
            .peek(story -> story.setAssigned(sprintStories.containsKey(story.getId())))
            .collect(Collectors.toList());
        
        long storyCount = JPAUtils.queryEntitiesCount(em, StoryEntity.class, queryParameters);
        
        return new EntityList<>(stories, storyCount);
    }
    
    @Override
    public EntityList<ExtendedStory> getProjectStories(String projectId, ProjectStoriesFilters params) {
        return ProjectStoryQueryBuilder
            .newBuilder(em)
            .build(projectId, params)
            .getQueryResult();
    }
    
    @Override
    public Story getStoryById(String storyId) {
        StoryEntity entity = em.find(StoryEntity.class, storyId);
        if (entity == null) {
            throw new NotFoundException("error.not-found");
        }
        if (entity.getStatus().equals(SimpleStatus.DISABLED)) {
            throw new NotFoundException("error.not-found");
        }
        return StoryMapper.fromEntity(entity);
    }
    
    @Override
    public StoryState getStoryState(String storyId) {
        
        StoryEntity story = getStoryEntityById(storyId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        StoryState state = new StoryState();
        state.setId(story.getId());
        state.setEstimated(story.getTimeEstimate() != null);
        state.setStoryStatus(story.getStoryStatus());
        
        TypedQuery<Boolean> query = em.createNamedQuery(StoryEntity.CHECK_IN_SPRINT, Boolean.class);
        Date now = new Date();
        query.setParameter("storyId", storyId);
        query.setParameter("now", now);
        
        try {
            state.setInActiveSprint(query.getSingleResult());
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
        
        return state;
    }
    
    @Override
    public Story getFullStoryById(String storyId) {
        Story story = getStoryEntityById(storyId)
            .map(StoryMapper::fromEntity)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        List<AcceptanceTest> tests = getStoryAcceptanceTests(storyId);
        story.setTests(tests);
        
        return story;
    }
    
    @Override
    public Optional<StoryEntity> getStoryEntityById(String storyId) {
        return Optional.ofNullable(em.find(StoryEntity.class, storyId));
    }
    
    @Override
    public Story createStory(String projectId, CreateStoryRequest request) {
        validator.assertNotBlank(request.getTitle());
        validator.assertNotNull(request.getPriority());
        
        if (request.getBusinessValue() <= 0) {
            throw new ValidationException("error.story.validation")
                .withEntity("Story")
                .withField("bussinessValue");
        }
        
        if (request.getTimeEstimate() != null) {
            if (request.getTimeEstimate() <= 0) {
                throw new ValidationException("error.story.validation")
                    .withEntity("Story")
                    .withField("timeEstimate");
            }
        }
        
        ProjectEntity project = projectService.getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        int newNumberId = getNewNumberId(projectId);
        
        StoryEntity entity = new StoryEntity();
        entity.setStatus(SimpleStatus.ACTIVE);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setPriority(request.getPriority());
        entity.setStoryStatus(StoryStatus.WAITING);
        entity.setBusinessValue(request.getBusinessValue());
        if (request.getTimeEstimate() != null) {
            entity.setTimeEstimate(request.getTimeEstimate());
        }
        entity.setProject(project);
        entity.setNumberId(newNumberId);
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            
            request.getTests().stream().map(test -> {
                AcceptanceTestEntity testEntity = new AcceptanceTestEntity();
                testEntity.setStory(entity);
                testEntity.setResult(test.getResult());
                return testEntity;
            }).forEach(testEntity -> em.persist(testEntity));
            
            em.getTransaction().commit();
            
            return StoryMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Story updateStory(String storyId, CreateStoryRequest story) {
        StoryEntity entity = getStoryEntityById(storyId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        validateStoryEdit(entity);
        
        try {
            em.getTransaction().begin();
            
            entity.setDescription(story.getDescription());
            entity.setTitle(story.getTitle());
            entity.setPriority(story.getPriority());
            entity.setTimeEstimate(story.getTimeEstimate());
            entity.setBusinessValue(story.getBusinessValue());
            
            entity.setTests(story.getTests()
                .stream()
                .map(test -> {
                    AcceptanceTestEntity testEntity = new AcceptanceTestEntity();
                    testEntity.setResult(test.getResult());
                    testEntity.setStory(entity);
                    return testEntity;
                })
                .collect(Collectors.toList())
            );
            
            em.getTransaction().commit();
            return StoryMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Story updateTimeEstimate(String storyId, Story story) {
        validator.assertNotNull(story.getTimeEstimate(), "timeEstimate", "Story");
        
        if (story.getTimeEstimate() <= 0) {
            throw new ValidationException("error.story.time-estimate.non-positive");
        }
        
        // TODO: check it's not added to sprint!
        
        StoryEntity entity = getStoryEntityById(storyId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        projectAuthorizationService.isScrumMasterOrThrow(
            entity.getProject().getId(),
            authContext.getId()
        );
        
        try {
            em.getTransaction().begin();
            entity.setTimeEstimate(story.getTimeEstimate());
            em.getTransaction().commit();
            return StoryMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public List<AcceptanceTest> getStoryAcceptanceTests(String storyId) {
        QueryParameters query = new QueryParameters();
        QueryUtil.overrideFilterParam(new QueryFilter("story.id", FilterOperation.EQ, storyId), query);
        return JPAUtils.getEntityStream(em, AcceptanceTestEntity.class, query)
            .map(StoryMapper::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Override
    public Story updateRealized(String storyId, Story story) {
        validator.assertNotNull(story.getStoryStatus(), "realized", "Story");
        
        StoryEntity entity = getStoryEntityById(storyId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        projectAuthorizationService.isProductOwnerOrThrow(
            entity.getProject().getId(),
            authContext.getId()
        );
        
        if (entity.getStoryStatus().equals(StoryStatus.REALIZED)) {
            throw new BadRequestException("error.bad-request");
        }
        
        if (entity.getStoryStatus().equals(StoryStatus.REJECTED) &&
            !story.getStoryStatus().equals(StoryStatus.REALIZED)) {
            throw new BadRequestException("error.bad-request");
        }
        
        try {
            em.getTransaction().begin();
            
            entity.setStoryStatus(story.getStoryStatus());
            if (story.getRejectComment() != null && story.getStoryStatus().equals(StoryStatus.REJECTED)) {
                entity.setRejectComment(story.getRejectComment());
            }
            em.getTransaction().commit();
            return StoryMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void removeStory(String storyId) {
        getStoryEntityById(storyId).ifPresent(entity -> {
            validateStoryEdit(entity);
            
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
    
    private void validateStoryEdit(StoryEntity entity) {
        projectAuthorizationService.isProjectAdminOrThrow(entity.getProject().getId(), authContext.getId());
        
        // TODO: check if in active sprint
        
        if (entity.getStoryStatus().equals(StoryStatus.REALIZED)) {
            throw new BadRequestException("error.story.realized").setEntity("Story");
        }
    }
    
    @Override
    public boolean checkStoryNameExists(String projectId, ConflictCheckRequest request) {
        validator.assertNotBlank(request.getValue());
        
        TypedQuery<StoryEntity> query = em.createNamedQuery(StoryEntity.GET_BY_TITLE, StoryEntity.class);
        query.setParameter("title", request.getValue().toLowerCase());
        query.setParameter("projectId", projectId);
        
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private int getNewNumberId(String projectId) {
        TypedQuery<Integer> query = em.createNamedQuery(StoryEntity.GET_NEW_NUMBER_ID, Integer.class);
        query.setParameter("projectId", projectId);
        try {
            return query.getSingleResult();
        } catch (PersistenceException e) {
            LOG.error("Unable to determine new number id!", e);
            throw new RestException("error.server");
        }
    }
    
    
}
