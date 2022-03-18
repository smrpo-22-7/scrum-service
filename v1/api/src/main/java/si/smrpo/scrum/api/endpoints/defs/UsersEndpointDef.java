package si.smrpo.scrum.api.endpoints.defs;

import com.mjamsek.rest.exceptions.dto.ExceptionResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.api.endpoints.examples.UserExamples;
import si.smrpo.scrum.lib.User;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.requests.ChangePasswordRequest;
import si.smrpo.scrum.lib.requests.UserRegisterRequest;
import si.smrpo.scrum.lib.requests.UsernameCheckRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface UsersEndpointDef {
    
    @GET
    @Tag(name = "users")
    @Operation(summary = "get users", description = "Returns paginated users.")
    @Parameter(name = "limit", in = ParameterIn.QUERY, schema = @Schema(type = SchemaType.INTEGER))
    @Parameter(name = "offset", in = ParameterIn.QUERY, schema = @Schema(type = SchemaType.INTEGER))
    @Parameter(name = "filter", in = ParameterIn.QUERY, schema = @Schema(type = SchemaType.STRING))
    @Parameter(name = "order", in = ParameterIn.QUERY, schema = @Schema(type = SchemaType.STRING))
    @APIResponses({
        @APIResponse(responseCode = "200", description = "returns users", content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = User.class, type = SchemaType.ARRAY), example = UserExamples.USER_LIST_RESPONSE),
            headers = {
                @Header(name = "X-Total-Count", description = "Number of all elements matching query", schema = @Schema(type = SchemaType.INTEGER))
            })
    })
    Response getUserList();
    
    @GET
    @Path("/{userId}")
    @Tag(name = "users")
    @Operation(summary = "get user", description = "Returns user with given id.")
    @Parameter(name = "id", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", description = "returns user", content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = User.class), example = UserExamples.USER_RESPONSE)),
        @APIResponse(responseCode = "404", description = "User does not exists", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ExceptionResponse.class)
        ))
    })
    Response getUser(@PathParam("userId") String userId);
    
    @GET
    @Path("/profile")
    @Tag(name = "users")
    @Operation(summary = "get user profile", description = "Retrieves user profile based on credentials presented.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "returns user profile", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserProfile.class, example = UserExamples.USER_PROFILE_RESPONSE)
        ))
    })
    Response getUserProfile();
    
    @POST
    @Tag(name = "users")
    @Operation(summary = "creates user", description = "Creates user account.")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
        implementation = UserRegisterRequest.class, example = UserExamples.USER_REGISTER_REQUEST)
    ))
    @APIResponses({
        @APIResponse(responseCode = "201", description = "user created"),
        @APIResponse(responseCode = "422", description = "validation failed", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ExceptionResponse.class)
        ))
    })
    Response createUser(UserRegisterRequest request);
    
    @PATCH
    @Path("/{userId}")
    @Tag(name = "users")
    @Operation(summary = "updates user", description = "Updates user.")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
        implementation = User.class, example = UserExamples.USER_RESPONSE)
    ))
    @APIResponses({
        @APIResponse(responseCode = "200", description = "user updated", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = User.class, example = UserExamples.USER_RESPONSE)
        )),
        @APIResponse(responseCode = "422", description = "validation failed", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ExceptionResponse.class)
        ))
    })
    Response updateUser(@PathParam("userId") String userId, User user);
    
    @POST
    @Path("/username-check")
    @Tag(name = "users")
    @Operation(summary = "check username exists", description = "Checks if the given username is already taken")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
        implementation = UsernameCheckRequest.class, example = UserExamples.USERNAME_CHECK_REQUEST)
    ))
    @APIResponses({
        @APIResponse(responseCode = "204", description = "username is not yet taken"),
        @APIResponse(responseCode = "409", description = "username is taken", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ExceptionResponse.class)
        ))
    })
    Response checkUsernameExists(UsernameCheckRequest request);
    
    @POST
    @Path("/update-credentials")
    @Tag(name = "users")
    @Operation(summary = "update user credentials", description = "Updates user credentials to given value.")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
        implementation = ChangePasswordRequest.class, example = UserExamples.CHANGE_PASSWORD_REQUEST)
    ))
    @APIResponses({
        @APIResponse(responseCode = "204", description = "credentials updated"),
        @APIResponse(responseCode = "422", description = "validation failed", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ExceptionResponse.class)
        ))
    })
    Response updateUserCredentials(ChangePasswordRequest request);
    
    @DELETE
    @Path("/{userId}/disable")
    @Tag(name = "users")
    @Operation(summary = "disable user", description = "Disables user account.")
    @Parameter(name = "id", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204", description = "disables user"),
        @APIResponse(responseCode = "404", description = "User does not exist", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ExceptionResponse.class)
        ))
    })
    Response disableUser(@PathParam("userId") String userId);
    
    @POST
    @Path("/{userId}/activate")
    @Tag(name = "users")
    @Operation(summary = "activates user", description = "Activates user account.")
    @Parameter(name = "id", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204", description = "activates user"),
        @APIResponse(responseCode = "404", description = "User does not exist", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ExceptionResponse.class)
        ))
    })
    Response activateUser(@PathParam("userId") String userId);
    
    @PATCH
    @Path("/profile")
    @Tag(name = "users")
    @Operation(summary = "updates user profile", description = "Updates user profile.")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(
        implementation = UserProfile.class, example = UserExamples.USER_PROFILE_RESPONSE)
    ))
    @APIResponses({
        @APIResponse(responseCode = "204", description = "user updated"),
        @APIResponse(responseCode = "422", description = "validation failed", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ExceptionResponse.class)
        ))
    })
    Response updateUserProfile(UserProfile userProfile);
}
