package com.AngelTwo;

import gabriel.application.Application;
import gabriel.hibernate.dao.LocationDao;
import gabriel.utilities.SystemUtils;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;


@Path("location")
public class LocationService {

	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLastKnownStatusOfAllVehicles() {
		JSONObject result;
		try {
			result = LocationDao.getLastKnownLocationForAllVehicles();
		} catch (Exception e) {
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toString()).build();
		}
		
    	return Response.status(Response.Status.OK).entity(result.toString()).build();
    }
	
    @Path("/vehicle/{id}/{fromDate}/{toDate}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getVehicleStatus(@PathParam("id") String uniqueId, @PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	JSONObject result;
		try {
			result = LocationDao.getLocationJsonForVehicle(uniqueId, sdf.parse(fromDate.trim()), sdf.parse(toDate.trim()));
		} catch (Exception e) {
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toString()).build();
		}
		
    	return Response.status(Response.Status.OK).entity(result.toString()).build();
    }
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadLocationPacket(InputStream is, @Context ServletContext servletContext){
		
		JSONObject inputStreamJSON = SystemUtils.convertInputStreamToJSON(is);
		if (inputStreamJSON != null) {
			

				double mLatitude = inputStreamJSON.getDouble("mLatitude");
				double mLongitude = inputStreamJSON.getDouble("mLongitude");
				double mAccuracy = inputStreamJSON.getDouble("mAccuracy");
				double mSpeed = inputStreamJSON.getDouble("mSpeed");
				double mDistance = inputStreamJSON.getDouble("mDistance");
				double mAltitude = inputStreamJSON.getDouble("mAltitude");
				long mTime = inputStreamJSON.getLong("mTime");
				double mBearing = inputStreamJSON.getDouble("mBearing");
				int signalStrength = 0;
				if(inputStreamJSON.has("signalStrength")){
					signalStrength = inputStreamJSON.getInt("signalStrength");
				}
				double batteryCharge = 0;
				if(inputStreamJSON.has("battery")){
					 batteryCharge = inputStreamJSON.getDouble("battery");
				}
				String uniqueId = inputStreamJSON.getString("uniqueId");
				String username = inputStreamJSON.getString("username");
				String rawPacket = inputStreamJSON.toString();
				
				String result = LocationDao.storeLocationPacket(mLatitude, mLongitude, mAccuracy, mSpeed, mDistance, 
						mAltitude, mTime, mBearing, signalStrength, batteryCharge, uniqueId, username, rawPacket);

				if(!result.trim().equals(Application.SUCCESS)){
					System.out.println("Failed to store LOCATION packet - "+ inputStreamJSON.toString());	
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
				}
				
				System.out.println("LOCATION Packet stored - "+inputStreamJSON.toString());
				return Response.status(Response.Status.OK).build();
			
		}
		
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
