package com.AngelTwo;

import gabriel.hibernate.dao.PingDao;
import gabriel.utilities.SystemUtils;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;


@Path("ping")
public class PingService {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response storePingPacket(InputStream is, @Context ServletContext servletContext){
		
		JSONObject inputStreamJSON = SystemUtils.convertInputStreamToJSON(is);
		if (inputStreamJSON != null) {
				double mLatitude = inputStreamJSON.getDouble("mLatitude");
				double mLongitude = inputStreamJSON.getDouble("mLongitude");
				double lastKnownLocationAccuracy = inputStreamJSON.getDouble("lastKnownLocationAccuracy");
				double batteryCharge = inputStreamJSON.getDouble("batteryCharge");
				long packetCreatedTime = inputStreamJSON.getLong("packetCreatedTime");	
				
				boolean packetStored = PingDao.storePingPacket(mLatitude, mLongitude, lastKnownLocationAccuracy, batteryCharge, packetCreatedTime);

				if(packetStored){
					System.out.println("PING Packet stored - "+inputStreamJSON.toString());
				} else {
					System.out.println("Failed to store PING packet - "+ inputStreamJSON.toString());
				}
				return Response.status(Response.Status.OK).build();
			
		}
		
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
