package com.AngelTwo;

import gabriel.application.Application;
import gabriel.hibernate.dao.DriverDao;
import gabriel.hibernate.dao.VehicleDao;
import gabriel.utilities.SystemUtils;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

/**
 * Root resource (exposed at "status" path)
 */
@Path("status")
public class StatusService {
	
    @GET
    @Path("/set/{team}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setTeam(@Context HttpServletRequest request, @PathParam("team") String team) {
    	return Response.status(Response.Status.OK)
				.entity(Application.setTeamNameForSession(request, team).toString()).build();
    }
    
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeam(@Context HttpServletRequest request) {
    	
		if(!Application.sessionIsValid(request)){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Application.getTeamNameForSession(request).toString()).build();
		}
		
    	return Response.status(Response.Status.OK)
				.entity(Application.getTeamNameForSession(request).toString()).build();
    }
    
    
    @Path("/{queryString}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getVehicleStatus(@Context HttpServletRequest request, 
    		@PathParam("queryString") String queryString, @Context ServletContext servletContext){
    	
		if(!Application.sessionIsValid(request)){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Application.getTeamNameForSession(request).toString()).build();
		}
		
    	JSONObject result = new JSONObject();
    	
    	JSONObject vehicles = VehicleDao.searchVehiclesFor(Application.getTeam(request), queryString);
    	JSONObject drivers = DriverDao.searchDriversFor(Application.getTeam(request), queryString);
    	
    	try {
			SystemUtils.createVehicleImageInTempCache(Application.getTeam(request), servletContext, vehicles);
			SystemUtils.createUserImageInTempCache(Application.getTeam(request), servletContext, drivers);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	result.put("vehicle", vehicles);
    	result.put("driver", drivers);
    	
    	return Response.status(Response.Status.OK).entity(result.toString()).build();
    }
}
