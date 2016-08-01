package com.AngelTwo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;


import gabriel.hibernate.dao.VehicleDao;

@Path("vehicle")
public class VehicleService {

	@GET
	public static Response createDummyVehicle(){
		VehicleDao.storeVehicleInfo("12346", null, "MH893298");
		return Response.status(Response.Status.OK).build();
	}
	
	
	@Path("/info/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public static Response getVehicleInfo(@PathParam("id") String uniqueId){
		JSONObject vehicleInfo = VehicleDao.getVehicleInfo(String.valueOf(uniqueId));
		return Response.status(Response.Status.OK).entity(vehicleInfo.toString()).build();
	}
}
