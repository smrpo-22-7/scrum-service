package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterOperation;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.ValidationException;
import com.mjamsek.rest.services.Validator;
import com.mjamsek.rest.utils.QueryUtil;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.requests.AddStoryRequest;
import si.smrpo.scrum.lib.responses.SprintListResponse;
import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.mappers.SprintMapper;
import si.smrpo.scrum.persistence.identifiers.SprintStoryId;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.sprint.SprintEntity;
import si.smrpo.scrum.persistence.sprint.SprintStoryEntity;
import si.smrpo.scrum.persistence.story.StoryEntity;
import si.smrpo.scrum.services.ProjectAuthorizationService;
import si.smrpo.scrum.services.ProjectService;
import si.smrpo.scrum.services.SprintService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestScoped
public class SprintServiceImpl implements SprintService {
    
    private static final Logger LOG = LogManager.getLogger(SprintServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private ProjectService projectService;
    
    @Inject
    private ProjectAuthorizationService authService;
    
    @Inject
    private AuthContext authContext;
    
    @Inject
    private Validator validator;
    
    @Override
    public EntityList<Sprint> getSprints(QueryParameters queryParameters) {
        List<Sprint> sprint = JPAUtils.getEntityStream(em, SprintEntity.class, queryParameters)
            .map(SprintMapper::fromEntity)
            .collect(Collectors.toList());
        
        Long sprintCount = JPAUtils.queryEntitiesCount(em, SprintEntity.class, queryParameters);
        
        return new EntityList<>(sprint, sprintCount);
    }
    
    @Override
    public SprintListResponse getProjectSprints(String projectId, boolean active, boolean past, boolean future) {
        QueryParameters queryParameters = new QueryParameters();
        QueryUtil.overrideFilterParam(new QueryFilter("project.id", FilterOperation.EQ, projectId), queryParameters);
        
        List<Sprint> projectSprints = JPAUtils.getEntityStream(em, SprintEntity.class, queryParameters)
            .map(SprintMapper::fromEntity)
            .collect(Collectors.toList());
        
        SprintListResponse sprints = new SprintListResponse();
        if (future) {
            sprints.setFutureSprints(new ArrayList<>());
        }
        if (past) {
            sprints.setPastSprints(new ArrayList<>());
        }
        
        Date now = new Date();
        projectSprints.forEach(sprint -> {
            Date startDate = Date.from(sprint.getStartDate());
            Date endDate = Date.from(sprint.getEndDate());
            
            if (now.after(startDate) && now.before(endDate) && active) {
                sprints.setActiveSprint(sprint);
            }
            if (now.before(startDate) && future) {
                sprints.getFutureSprints().add(sprint);
            }
            if (now.after(endDate) && past) {
                sprints.getPastSprints().add(sprint);
            }
        });
        
        return sprints;
    }
    
    @Override
    public Sprint getSprintById(String sprintId) {
        SprintEntity entity = em.find(SprintEntity.class, sprintId);
        if (entity == null) {
            throw new NotFoundException("error.not-found");
        }
        if (entity.getStatus().equals(SimpleStatus.DISABLED)) {
            throw new NotFoundException("error.not-found");
        }
        return SprintMapper.fromEntity(entity);
    }
    
    @Override
    public Optional<SprintEntity> getSprintEntityById(String sprintId) {
        return Optional.ofNullable(em.find(SprintEntity.class, sprintId));
    }
    
    @Override
    public Sprint createSprint(String projectId, Sprint sprint) {
        validator.assertNotBlank(sprint.getTitle());
        Date now = new Date();
        validator.assertNotBefore(Date.from(sprint.getStartDate()), now);
        validator.assertNotBefore(Date.from(sprint.getEndDate()), Date.from(sprint.getStartDate()));
        
        if (sprint.getExpectedSpeed() <= 0) {
            throw new ValidationException("error.sprint.validation");
        }
    
        ProjectEntity project = projectService.getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        SprintEntity entity = new SprintEntity();
        entity.setStatus(SimpleStatus.ACTIVE);
        entity.setTitle(sprint.getTitle());
        entity.setStartDate(Date.from(sprint.getStartDate()));
        entity.setEndDate(Date.from(sprint.getEndDate()));
        entity.setExpectedSpeed(sprint.getExpectedSpeed());
        entity.setProject(project);
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return SprintMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void addStoriesToSprint(String sprintId, AddStoryRequest request) {
        SprintEntity sprint = getSprintEntityById(sprintId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        if (sprint.getStatus().equals(SimpleStatus.DISABLED)) {
            throw new NotFoundException("error.not-found");
        }
        
        authService.isScrumMasterOrThrow(sprint.getProject().getId(), authContext.getId());
        
        QueryParameters q = new QueryParameters();
        QueryUtil.overrideFilterParam(new QueryFilter("id", FilterOperation.IN, request.getStoryIds()), q);
        QueryUtil.overrideFilterParam(new QueryFilter("status", FilterOperation.EQ, SimpleStatus.ACTIVE.name()), q);
        QueryUtil.overrideFilterParam(new QueryFilter("timeEstimate", FilterOperation.ISNOTNULL), q);
        //QueryUtil.overrideFilterParam(new QueryFilter("realized", FilterOperation.EQ, ....), q);
        
        List<StoryEntity> stories = JPAUtils.queryEntities(em, StoryEntity.class, q);
        
        try {
            em.getTransaction().begin();
            
            stories.stream().map(s -> {
                var s2 = new SprintStoryEntity();
                var id = new SprintStoryId();
                id.setSprint(sprint);
                id.setStory(s);
                s2.setId(id);
                
                return s2;
                
            }).forEach(s -> em.persist(s));
            
            em.getTransaction().commit();
            
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
}
