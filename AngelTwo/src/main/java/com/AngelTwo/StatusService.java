package com.AngelTwo;

import gabriel.hibernate.dao.LocationDao;
import gabriel.hibernate.dao.PingDao;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Root resource (exposed at "status" path)
 */
@Path("status")
public class StatusService {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "online";
    }
    
    @Path("/vehicle/{accuracy}/{maxresults}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getVehicleStatus(@PathParam("maxresults") int maxResults, @PathParam("accuracy") int accuracy){
    	JSONObject vehicleStatus = new JSONObject();
    	JSONArray locationHistory = LocationDao.getLocationJson(maxResults, accuracy);
    	JSONArray pingHistory = PingDao.getPingJson(maxResults);
    	vehicleStatus.put("location", locationHistory);
    	vehicleStatus.put("pings", pingHistory);
    	return Response.status(Response.Status.OK).entity(vehicleStatus.toString()).build();
    }
}
