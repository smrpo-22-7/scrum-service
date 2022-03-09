package si.smrpo.scrum.api.endpoints.defs;

import com.mjamsek.rest.exceptions.dto.ExceptionResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.api.endpoints.examples.UserExamples;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.requests.ChangePasswordRequest;
import si.smrpo.scrum.lib.requests.UserRegisterRequest;
import si.smrpo.scrum.lib.requests.UsernameCheckRequest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface UsersEndpointDef {
    
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
}
