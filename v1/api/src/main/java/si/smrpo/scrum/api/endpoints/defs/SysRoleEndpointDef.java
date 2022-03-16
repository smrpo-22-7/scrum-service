package si.smrpo.scrum.api.endpoints.defs;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.api.endpoints.examples.SysRoleExamples;
import si.smrpo.scrum.lib.SysRole;

import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface SysRoleEndpointDef {
    
    @GET
    @Tag(name = "sys-roles")
    @Operation(summary = "get system roles", description = "Returns list of all available system roles")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "returns system roles", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SysRole.class, type = SchemaType.ARRAY, example = SysRoleExamples.ALL_ROLES_RESPONSE)
        ))
    })
    Response getSysRoles();
    
}
