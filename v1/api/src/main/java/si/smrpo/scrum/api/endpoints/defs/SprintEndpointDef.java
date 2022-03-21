package si.smrpo.scrum.api.endpoints.defs;

import si.smrpo.scrum.lib.sprints.Sprint;

import javax.ws.rs.*;

import javax.ws.rs.core.Response;

public interface SprintEndpointDef {

    @GET
    @Path("/{projectId}/sprints")
    Response getSprintsList(@PathParam("projectId") String projectId);

    @GET
    @Path("/{sprintId}")
    Response getSprintById(@PathParam("sprintId") String sprintId);

    @POST
    @Path("/{projectId}/newsprint")
    Response createSprint(@PathParam("projectId") String projectId, Sprint sprint);
}
