package si.smrpo.scrum.api.endpoints;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.api.endpoints.defs.UsersEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.User;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.requests.ChangePasswordRequest;
import si.smrpo.scrum.lib.requests.UserRegisterRequest;
import si.smrpo.scrum.lib.requests.UsernameCheckRequest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.mjamsek.rest.Rest.HttpHeaders.X_TOTAL_COUNT;

@SecureResource
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class UsersEndpoint implements UsersEndpointDef {
    
    @Inject
    private UserService userService;
    
    @Inject
    private QueryParameters queryParameters;
    
    @Inject
    private AuthContext authContext;
    
    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response getUserList() {
        EntityList<User> users = userService.getUserList(queryParameters);
        return Response.ok(users.getEntities())
            .header(X_TOTAL_COUNT, users.getCount())
            .build();
    }
    
    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response getUser(String userId) {
        User user = userService.getUserById(userId);
        return Response.ok(user).build();
    }
    
    @Override
    public Response getUserProfile() {
        UserProfile profile = userService.getUserProfile(authContext.getId());
        return Response.ok(profile).build();
    }
    
    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response createUser(UserRegisterRequest request) {
        userService.registerUser(request);
        return Response.status(Response.Status.CREATED).build();
    }
    
    @Override
    public Response updateUser(String userId, User user) {
        User updatedUser = userService.updateUser(userId, user);
        return Response.ok(updatedUser).build();
    }
    
    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response checkUsernameExists(UsernameCheckRequest request) {
        boolean exists = userService.usernameExists(request.getUsername());
        if (exists) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.noContent().build();
    }
    
    @Override
    public Response updateUserCredentials(ChangePasswordRequest request) {
        userService.changePassword(authContext.getId(), request);
        return Response.noContent().build();
    }
    
    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response disableUser(String userId) {
        userService.changeUserStatus(userId, SimpleStatus.DISABLED);
        return Response.noContent().build();
    }
    
    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response activateUser(String userId) {
        userService.changeUserStatus(userId, SimpleStatus.ACTIVE);
        return Response.noContent().build();
    }
    
    @Override
    public Response updateUserProfile(UserProfile userProfile) {
        userService.updateUserProfile(authContext.getId(), userProfile);
        return Response.noContent().build();
    }
}
