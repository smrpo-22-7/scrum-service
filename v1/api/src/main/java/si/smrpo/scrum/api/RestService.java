package si.smrpo.scrum.api;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import si.smrpo.scrum.api.endpoints.SysRoleEndpoint;
import si.smrpo.scrum.api.endpoints.UsersEndpoint;
import si.smrpo.scrum.api.mappers.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/v1")
@OpenAPIDefinition(info = @Info(title = "Scrum service", version = "1.0.0", description = "API for Scrum application",
    contact = @Contact(url = "https://github.com/smrpo-22-7")),
    servers = {
        @Server(url = "http://localhost:8080/v1", description = "Local development environment"),
        @Server(url = "http://188.34.196.239:8080/v1", description = "Test environment"),
    })
public class RestService extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // endpoints
        classes.add(UsersEndpoint.class);
        classes.add(SysRoleEndpoint.class);
        
        // exception mappers
        classes.add(DefaultExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);
        classes.add(ForbiddenExceptionMapper.class);
        classes.add(ValidationExceptionMapper.class);
        classes.add(UnauthorizedExceptionMapper.class);
        
        return classes;
    }
}
