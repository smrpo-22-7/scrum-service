package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.ValidationException;
import com.mjamsek.rest.services.Validator;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.mappers.SprintMapper;
import si.smrpo.scrum.persistence.sprint.SprintEntity;
import si.smrpo.scrum.services.ProjectService;
import si.smrpo.scrum.services.SprintService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
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
    public Sprint createSprint(Sprint sprint) {
        validator.assertNotBlank(sprint.getTitle());
        Date now = new Date();
        validator.assertNotBefore(sprint.getStartDate(), now);
        validator.assertNotBefore(sprint.getEndDate(), sprint.getStartDate());

        if (sprint.getExpectedSpeed() <= 0) {
            throw new ValidationException("error.sprint.validation");
        }

        SprintEntity entity = new SprintEntity();
        entity.setStatus(SimpleStatus.ACTIVE);
        entity.setTitle(sprint.getTitle());
        entity.setStartDate(sprint.getStartDate());
        entity.setEndDate(sprint.getEndDate());
        entity.setExpectedSpeed(sprint.getExpectedSpeed());
        entity.setProject(projectService.getProjectEntityById(sprint.getProjectId())
                                        .orElseThrow(() -> new NotFoundException("error.not-found")));

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
}
