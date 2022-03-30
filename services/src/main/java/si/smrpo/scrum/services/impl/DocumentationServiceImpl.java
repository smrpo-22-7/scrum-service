package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.integrations.markdown.MarkdownRenderService;
import si.smrpo.scrum.lib.ProjectDocumentation;
import si.smrpo.scrum.lib.responses.DocumentationContentResponse;
import si.smrpo.scrum.mappers.DocumentationMapper;
import si.smrpo.scrum.persistence.docs.ProjectDocumentationEntity;
import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.services.DocumentationService;
import si.smrpo.scrum.services.ProjectService;
import si.smrpo.scrum.utils.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestScoped
public class DocumentationServiceImpl implements DocumentationService {
    
    private static final Logger LOG = LogManager.getLogger(DocumentationServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private MarkdownRenderService markdownRenderService;
    
    @Inject
    private ProjectService projectService;
    
    @Override
    public void saveDocumentation(String projectId, ProjectDocumentation documentation) {
        ProjectEntity project = projectService.getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        getDocumentationEntity(projectId).ifPresentOrElse(
            entity -> updateExistingDocumentation(entity, documentation.getMarkdownContent()),
            () -> saveNewDocumentation(project, documentation.getMarkdownContent())
        );
    }
    
    @Override
    public void saveDocumentation(String projectId, InputStream markdownInputStream) {
        ProjectEntity project = projectService.getProjectEntityById(projectId)
            .orElseThrow(() -> new NotFoundException("error.not-found"));
        
        String markdownContent = new BufferedReader(new InputStreamReader(markdownInputStream))
            .lines().collect(Collectors.joining("\n"));
        
        getDocumentationEntity(projectId).ifPresentOrElse(
            entity -> updateExistingDocumentation(entity, markdownContent),
            () -> saveNewDocumentation(project, markdownContent)
        );
    }
    
    @Override
    public ProjectDocumentation getDocumentation(String projectId) {
        return getDocumentationEntity(projectId)
            .map(entity -> DocumentationMapper.base(entity, true, false, false))
            .orElseThrow(() -> new NotFoundException("error.not-found"));
    }
    
    @Override
    public Optional<ProjectDocumentationEntity> getDocumentationEntity(String projectId) {
        TypedQuery<ProjectDocumentationEntity> query = em.createNamedQuery(ProjectDocumentationEntity.GET_BY_PROJECT, ProjectDocumentationEntity.class);
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
    
    @Override
    public DocumentationContentResponse getDocumentationContentMarkdown(String projectId) {
        return getDocumentationEntity(projectId)
            .map(entity -> {
                DocumentationContentResponse resp = new DocumentationContentResponse();
                resp.setBytes(entity.getMarkdownContent().getBytes(StandardCharsets.UTF_8));
                String filename = entity.getProject().getId() + "_"
                    + StringUtils.removeSpaces(entity.getProject().getName().toLowerCase(Locale.ROOT), "-")
                    + "_docs.md";
                resp.setFilename(filename);
                return resp;
            })
            .orElseGet(() -> {
                DocumentationContentResponse resp = new DocumentationContentResponse();
                resp.setBytes(new byte[]{});
                String filename = "blank_docs.md";
                resp.setFilename(filename);
                return resp;
            });
    }
    
    @Override
    public DocumentationContentResponse getDocumentationContentHtml(String projectId) {
        return getDocumentationEntity(projectId)
            .map(entity -> {
                DocumentationContentResponse resp = new DocumentationContentResponse();
                resp.setBytes(entity.getHtmlContent().getBytes(StandardCharsets.UTF_8));
                String filename = entity.getProject().getId() + "_"
                    + StringUtils.removeSpaces(entity.getProject().getName().toLowerCase(Locale.ROOT), "-")
                    + "_docs.html";
                resp.setFilename(filename);
                return resp;
            })
            .orElseGet(() -> {
                DocumentationContentResponse resp = new DocumentationContentResponse();
                resp.setBytes(new byte[]{});
                String filename = "blank_docs.html";
                resp.setFilename(filename);
                return resp;
            });
    }
    
    private void saveNewDocumentation(ProjectEntity project, String markdownContent) {
        ProjectDocumentationEntity entity = new ProjectDocumentationEntity();
        entity.setProject(project);
        entity.setMarkdownContent(markdownContent);
        
        try {
            String htmlContent = markdownRenderService.convertMarkdownToHtml(markdownContent);
            entity.setHtmlContent(htmlContent);
            String textContent = markdownRenderService.convertMarkdownToText(markdownContent);
            entity.setTextContent(textContent);
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        } catch (Exception e) {
            LOG.error("Error when parsing markdown!", e);
            throw new RestException("error.server");
        }
    }
    
    private void updateExistingDocumentation(ProjectDocumentationEntity documentationEntity, String markdownContent) {
        try {
            em.getTransaction().begin();
            documentationEntity.setMarkdownContent(markdownContent);
            String htmlContent = markdownRenderService.convertMarkdownToHtml(markdownContent);
            documentationEntity.setHtmlContent(htmlContent);
            String textContent = markdownRenderService.convertMarkdownToText(markdownContent);
            documentationEntity.setTextContent(textContent);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        } catch (Exception e) {
            LOG.error("Error when parsing markdown!", e);
            throw new RestException("error.server");
        }
    }
}
