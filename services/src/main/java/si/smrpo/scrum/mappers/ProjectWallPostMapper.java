package si.smrpo.scrum.mappers;

import si.smrpo.scrum.integrations.auth.mappers.UserMapper;
import si.smrpo.scrum.lib.projects.ProjectWallPost;
import si.smrpo.scrum.persistence.project.ProjectWallPostEntity;

public class ProjectWallPostMapper {
    
    public static ProjectWallPost fromEntity(ProjectWallPostEntity entity, boolean withMarkdown, boolean withHtml, boolean withText) {
        ProjectWallPost post = BaseMapper.fromEntity(entity, ProjectWallPost.class);
        post.setStatus(entity.getStatus());
        if (entity.getProject() != null) {
            post.setProjectId(entity.getProject().getId());
        }
        if (entity.getAuthor() != null) {
            post.setAuthorId(entity.getAuthor().getId());
            post.setAuthor(UserMapper.toSimpleProfile(entity.getAuthor()));
        }
        if (withMarkdown) {
            post.setMarkdownContent(entity.getMarkdownContent());
        }
        if (withHtml) {
            post.setHtmlContent(entity.getHtmlContent());
        }
        if (withText) {
            post.setTextContent(entity.getTextContent());
        }
        return post;
    }
    
}
