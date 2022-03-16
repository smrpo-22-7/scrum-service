package si.smrpo.scrum.api.endpoints;

import si.smrpo.scrum.api.endpoints.defs.SysRoleEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.integrations.auth.services.RoleService;
import si.smrpo.scrum.lib.SysRole;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/sys-roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@SecureResource
public class SysRoleEndpoint implements SysRoleEndpointDef {
    
    @Inject
    private RoleService roleService;
    
    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response getSysRoles() {
        Set<SysRole> roles = roleService.getAllSysRoles();
        return Response.ok(roles).build();
    }
}
