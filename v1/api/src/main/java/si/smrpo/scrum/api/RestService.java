package si.smrpo.scrum.api;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/v1")
@OpenAPIDefinition(info = @Info(title = "Scrum service", version = "1.0.0", description = "API for Scrum application",
    contact = @Contact(url = "https://github.com/smrpo-22-7")),
    servers = {
        @Server(url = "http://localhost:8080/v1", description = "Local development environment")
    })
public class RestService extends Application {

}
