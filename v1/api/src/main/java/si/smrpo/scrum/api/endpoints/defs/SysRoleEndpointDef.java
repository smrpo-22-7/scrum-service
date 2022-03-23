package si.smrpo.scrum.api.endpoints.defs;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.lib.SysRole;
import si.smrpo.scrum.lib.requests.ChangeSysRoleRequest;

import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface SysRoleEndpointDef {
    
    @GET
    @Tag(name = "sys-roles")
    @Operation(summary = "get system roles", description = "Returns list of all available system roles")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "returns system roles", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = SysRole.class, type = SchemaType.ARRAY)))
    })
    Response getSysRoles();
    
    @PATCH
    @Path("/users/{userId}")
    @Tag(name = "sys-roles")
    @Parameter(name = "userId", in = ParameterIn.PATH, required = true)
    @Operation(summary = "update user system roles", description = "Updates user's system roles")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ChangeSysRoleRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "204", description = "User roles were updated"),
        @APIResponse(responseCode = "404", description = "User does not exists"),
    })
    Response updateUserRoles(@PathParam("userId") String userId, ChangeSysRoleRequest request);
    
}
