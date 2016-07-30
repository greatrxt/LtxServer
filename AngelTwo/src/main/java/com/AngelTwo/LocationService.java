package com.AngelTwo;

import gabriel.application.Application;
import gabriel.hibernate.dao.LocationDao;
import gabriel.utilities.SystemUtils;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;


@Path("location")
public class LocationService {

	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Restful API working";
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
				
				String result = LocationDao.storeLocationPacket(mLatitude, mLongitude, mAccuracy, mSpeed, mDistance, mAltitude, mTime, mBearing);

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
