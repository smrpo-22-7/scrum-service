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
import liquibase.pro.packaged.A;
import liquibase.pro.packaged.Q;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.mappers.StoryMapper;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.story.AcceptanceTestEntity;
import si.smrpo.scrum.persistence.story.StoryEntity;
import si.smrpo.scrum.services.ProjectService;
import si.smrpo.scrum.services.SprintService;
import si.smrpo.scrum.services.StoryService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StoryServiceImpl implements StoryService {

    private static final Logger LOG = LogManager.getLogger(SprintServiceImpl.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private SprintService ss;

    @Inject
    private ProjectService projectService;

    @Inject
    private Validator validator;

    @Override
    public EntityList<Story> getStories(String projectId, QueryParameters queryParameters) {
        QueryUtil.overrideFilterParam(new QueryFilter("project.id", FilterOperation.EQ, projectId), queryParameters);
        List<Story> story = JPAUtils.getEntityStream(em, StoryEntity.class, queryParameters)
                .map(StoryMapper::fromEntity).collect(Collectors.toList());

        long storyCount = JPAUtils.queryEntitiesCount(em, StoryEntity.class, queryParameters);

        return new EntityList<>(story, storyCount);
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
    public Optional<StoryEntity> getStoryEntityById(String storyId) {
        return Optional.ofNullable(em.find(StoryEntity.class, storyId));
    }

    @Override
    public Story createStory(String projectId, CreateStoryRequest request) {
        validator.assertNotBlank(request.getTitle());
        validator.assertNotNull(request.getPriority());

        if (request.getBusinessValue() <= 0) {
            throw new ValidationException("error.story.validation");
        }

        ProjectEntity project = projectService.getProjectEntityById(projectId)
                .orElseThrow(() -> new NotFoundException("error.not-found"));

        StoryEntity entity = new StoryEntity();
        entity.setStatus(SimpleStatus.ACTIVE);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setPriority(request.getPriority());
        entity.setBusinessValue(request.getBusinessValue());
        entity.setProject(project);

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
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }

}
