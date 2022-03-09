package si.smrpo.scrum.api.endpoints;

import com.kumuluz.ee.rest.beans.QueryParameters;
import si.smrpo.scrum.api.endpoints.defs.UsersEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.requests.UserRegisterRequest;
import si.smrpo.scrum.lib.requests.UsernameCheckRequest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SecureResource
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class UsersEndpoint implements UsersEndpointDef {
    
    @Inject
    private QueryParameters queryParameters;
    
    @Inject
    private UserService userService;
    
    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response createUser(UserRegisterRequest request) {
        userService.registerUser(request);
        return Response.status(Response.Status.CREATED).build();
    }
    
    @Override
    public Response checkUsernameExists(UsernameCheckRequest request) {
        boolean exists = userService.usernameExists(request.getUsername());
        if (exists) {
            Response.status(Response.Status.CONFLICT).build();
        }
        return Response.noContent().build();
    }
}
