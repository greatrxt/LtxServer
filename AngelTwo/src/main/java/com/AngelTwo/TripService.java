package com.AngelTwo;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import gabriel.hibernate.dao.TripDao;

@Path("trip")
public class TripService {

	@GET
	public static Response testTripDao(){
		TripDao.saveTripData("8705760967", "Brad", new Date(), 12.2332, 11.32322);
		return Response.status(Response.Status.OK).build();
	}
}
