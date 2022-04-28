package si.smrpo.scrum.api;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import si.smrpo.scrum.api.endpoints.*;
import si.smrpo.scrum.api.mappers.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationPath("/v1")
@OpenAPIDefinition(info = @Info(title = "Scrum service", version = "1.0.0", description = "API for Scrum application",
    contact = @Contact(url = "https://github.com/smrpo-22-7")),
    servers = {
        @Server(url = "https://smrpo.mjamsek.com", description = "Test environment"),
        @Server(url = "http://localhost:8080", description = "Local development environment"),
    })
public class RestService extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // endpoints
        classes.add(UsersEndpoint.class);
        classes.add(SysRoleEndpoint.class);
        classes.add(ProjectsEndpoint.class);
        classes.add(SprintEndpoint.class);
        classes.add(StoryEndpoint.class);
        classes.add(ProjectWallEndpoint.class);
        classes.add(DocumentationEndpoint.class);
        classes.add(TaskEndpoint.class);
        classes.add(HoursEndpoint.class);
        
        // exception mappers
        classes.add(DefaultExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);
        classes.add(ForbiddenExceptionMapper.class);
        classes.add(ValidationExceptionMapper.class);
        classes.add(UnauthorizedExceptionMapper.class);
        classes.add(WebApplicationExceptionMapper.class);
        
        return classes;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature");
        return props;
    }
}
