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
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.services.Validator;
import com.mjamsek.rest.utils.QueryUtil;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.integrations.markdown.MarkdownRenderService;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.projects.ProjectWallPost;
import si.smrpo.scrum.mappers.ProjectWallPostMapper;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.project.ProjectWallPostEntity;
import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.services.ProjectService;
import si.smrpo.scrum.services.ProjectWallService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestScoped
public class ProjectWallServiceImpl implements ProjectWallService {
    
    private static final Logger LOG = LogManager.getLogger(ProjectWallServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private ProjectService projectService;
    
    @Inject
    private MarkdownRenderService markdownRenderService;
    
    @Inject
    private Validator validator;
    
    @Inject
    private AuthContext authContext;
    
    @Override
    public EntityList<ProjectWallPost> getPosts(String projectId, QueryParameters queryParameters) {
        QueryUtil.overrideFilterParam(new QueryFilter("project.id", FilterOperation.EQ, projectId), queryParameters);
        QueryUtil.overrideFilterParam(new QueryFilter("status", FilterOperation.EQ, SimpleStatus.ACTIVE.name()), queryParameters);
        
        List<ProjectWallPost> posts = JPAUtils.getEntityStream(em, ProjectWallPostEntity.class, queryParameters)
            .map(entity -> {
                return ProjectWallPostMapper.fromEntity(entity, false, true, false);
            })
            .collect(Collectors.toList());
        
        long postCount = JPAUtils.queryEntitiesCount(em, ProjectWallPostEntity.class, queryParameters);
        
        return new EntityList<>(posts, postCount);
    }
    
    @Override
    public ProjectWallPost getPost(String postId) {
        return getPostEntityById(postId)
            .map(entity -> {
                return ProjectWallPostMapper.fromEntity(entity, true, true, false);
            })
            .orElseThrow(() -> new NotFoundException("error.not-found"));
    }
    
    @Override
    public void addPost(String projectId, ProjectWallPost post) {
        validator.assertNotBlank(post.getMarkdownContent());
    
        ProjectEntity project = projectService.getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        UserEntity author = getCurrentUser();
        
        ProjectWallPostEntity entity = new ProjectWallPostEntity();
        entity.setProject(project);
        entity.setAuthor(author);
        entity.setStatus(SimpleStatus.ACTIVE);
        entity.setMarkdownContent(post.getMarkdownContent());
        String htmlContent = markdownRenderService.convertMarkdownToHtml(post.getMarkdownContent());
        entity.setHtmlContent(htmlContent);
        String textContent = markdownRenderService.convertMarkdownToText(post.getMarkdownContent());
        entity.setTextContent(textContent);
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void removePost(String postId) {
        getPostEntityById(postId).ifPresent(entity -> {
            try {
                em.getTransaction().begin();
                entity.setStatus(SimpleStatus.DISABLED);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("error.server");
            }
        });
    }
    
    @Override
    public Optional<ProjectWallPostEntity> getPostEntityById(String id) {
        return Optional.ofNullable(em.find(ProjectWallPostEntity.class, id));
    }
    
    private UserEntity getCurrentUser() {
        UserEntity user = em.find(UserEntity.class, authContext.getId());
        if (user == null) {
            throw new UnauthorizedException("error.unauthorized");
        }
        return user;
    }
}
