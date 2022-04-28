package si.smrpo.scrum.api.endpoints;

import si.smrpo.scrum.api.endpoints.defs.HoursEndpointDef;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.lib.stories.TaskWorkSpent;
import si.smrpo.scrum.services.TaskService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SecureResource
@Path("/hours")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class HoursEndpoint implements HoursEndpointDef {
    
    @Inject
    private TaskService taskService;
    
    @Override
    public Response updateHours(String hourId, TaskWorkSpent taskWork) {
        TaskWorkSpent updatedWork = taskService.updateTaskHours(hourId, taskWork);
        return Response.ok(updatedWork).build();
    }
    
    @Override
    public Response deleteTaskHours(String hourId) {
        taskService.removeTaskHours(hourId);
        return Response.noContent().build();
    }
}
