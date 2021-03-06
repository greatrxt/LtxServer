package com.AngelTwo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import gabriel.application.Application;
import gabriel.hibernate.dao.VehicleDao;
import gabriel.utilities.SystemUtils;

@Path("vehicle")
public class VehicleService {
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public static Response editVehicle(@Context HttpServletRequest request, InputStream is, @Context ServletContext context){
		
		if(!Application.sessionIsValid(request)){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Application.getTeamNameForSession(request).toString()).build();
		}
		
		JSONObject result = new JSONObject();
		try {
			String contextPath = context.getContextPath();
			String contextRealPath = context.getRealPath(contextPath);
			String imagePath = contextRealPath + File.separator + Application.FOLDER_UPLOADS + File.separator + Application.FOLDER_VEHICLE_IMAGES; 
			File imageUploadFolder = new File(imagePath);
			
			if(!imageUploadFolder.exists()){
				System.out.println("Creating image folder");
				imageUploadFolder.mkdirs();
			}

			JSONObject inputJson = SystemUtils.convertInputStreamToJSON(is);		
			String missingFields = "";
			String uniqueId = null, registrationNumber = null, image = null;
			
			if(inputJson.has("uniqueId")){
				uniqueId = inputJson.getString("uniqueId");
				if(uniqueId.trim().isEmpty()){
					missingFields+="uniqueId ";	
				}
			} else {
				missingFields+=" uniqueId ";
			}
			
			if(inputJson.has("registration")){
				registrationNumber = inputJson.getString("registration");
				if(registrationNumber.trim().isEmpty()){
					missingFields+=" registration ";	
				}
			} else {
				missingFields+="registration ";
			}
			
			if(missingFields.trim().length() != 0){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Critical Fields Missing : "+missingFields);
				return Response.status(Response.Status.BAD_REQUEST).entity(result.toString()).build();
			}
			
			//process image
			if(inputJson.has("image")){
				String tempImageString = inputJson.getString("image").trim();
				if(tempImageString.equals("1")){
					image = "1";
				} else if (tempImageString.equals("0")){
					image = null;
				} else {
					image = tempImageString.split(",")[1];
				}
			} else {
				image = Application.STANDARD_IMAGE_NOT_FOUND.split(",")[1];
			}
			
			result = VehicleDao.editVehicle(Application.getTeam(request), uniqueId, image, registrationNumber);
			
			if(result.getString(Application.RESULT).equals(Application.SUCCESS)){
				return Response.status(Response.Status.OK).entity(result.toString()).build();	
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toString()).build();
			}
		
		} catch(Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toString()).build();
		}	
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public static Response createVehicle(@Context HttpServletRequest request, InputStream is, @Context ServletContext context){
	
		if(!Application.sessionIsValid(request)){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Application.getTeamNameForSession(request).toString()).build();
		}
		
		JSONObject result = new JSONObject();
		try {
			String contextPath = context.getContextPath();
			String contextRealPath = context.getRealPath(contextPath);
			String imagePath = contextRealPath + File.separator + Application.FOLDER_UPLOADS + File.separator + Application.FOLDER_VEHICLE_IMAGES; 
			File imageUploadFolder = new File(imagePath);
			
			if(!imageUploadFolder.exists()){
				System.out.println("Creating image folder");
				imageUploadFolder.mkdirs();
			}

			JSONObject inputJson = SystemUtils.convertInputStreamToJSON(is);		
			String missingFields = "";
			String uniqueId = "", registrationNumber = "", image = null;
			
			if(inputJson.has("uniqueId")){
				uniqueId = inputJson.getString("uniqueId");
			} 
			
			if(uniqueId.trim().isEmpty()){
				missingFields+="uniqueId ";	
			}
			
			if(inputJson.has("registration")){
				registrationNumber = inputJson.getString("registration").replaceAll("[^A-Za-z0-9 ]", ""); //remove all non-alphanumber characters. Leave spaces intact
			} 
			
			if(registrationNumber.trim().isEmpty()){
				missingFields+=" registration ";	
			}
			
			if(missingFields.trim().length() != 0){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Critical Fields Missing : "+missingFields);
				return Response.status(Response.Status.BAD_REQUEST).entity(result.toString()).build();
			}
			
			//process image
			if(inputJson.has("image")){
				String tempImageString = inputJson.getString("image");
				if(tempImageString.trim().isEmpty()){
					image = Application.STANDARD_IMAGE_NOT_FOUND.split(",")[1];
				} else {
					image = inputJson.getString("image").split(",")[1];
				}
			} else {
				image = Application.STANDARD_IMAGE_NOT_FOUND.split(",")[1];
			}
			
			result = VehicleDao.storeVehicleInfo(Application.getTeam(request), uniqueId, image, registrationNumber);
			
			if(result.getString(Application.RESULT).equals(Application.SUCCESS)){
				return Response.status(Response.Status.CREATED).entity(result.toString()).build();	
			} else {
				return Response.status(Response.Status.CONFLICT).entity(result.toString()).build();
			}
		
		} catch(Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toString()).build();
		}		
		
	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public static Response getAllVehicles(@Context HttpServletRequest request, @Context ServletContext context){
		
		if(!Application.sessionIsValid(request)){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Application.getTeamNameForSession(request).toString()).build();
		}
		
		JSONObject result = null;
		
		try {
			result = VehicleDao.getAllVehicles(Application.getTeam(request));
			SystemUtils.createVehicleImageInTempCache(Application.getTeam(request), context, result);			
			return Response.status(Response.Status.OK).entity(result.toString()).build();
			
		} catch(Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toString()).build();
		}	
	}
	
	@Path("/{uniqueId}")
	@Produces(MediaType.APPLICATION_JSON)
	@DELETE
	public static Response deleteVehicle(@Context HttpServletRequest request, @Context ServletContext context, @PathParam("uniqueId") String uniqueId){
		
		if(!Application.sessionIsValid(request)){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Application.getTeamNameForSession(request).toString()).build();
		}
		
		JSONObject result = null;
		try {
			result = VehicleDao.deleteVehicle(Application.getTeam(request), String.valueOf(uniqueId));
			return Response.status(Response.Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toString()).build();
		}
	}
	
	@Path("/{uniqueId}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public static Response getVehicleInfo(@Context HttpServletRequest request, @Context ServletContext context, @PathParam("uniqueId") String uniqueId){
		
		if(!Application.sessionIsValid(request)){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Application.getTeamNameForSession(request).toString()).build();
		}
		
		JSONObject result = null;
		try {
			result = VehicleDao.getVehicleInfo(Application.getTeam(request), String.valueOf(uniqueId));
			SystemUtils.createVehicleImageInTempCache(Application.getTeam(request), context, result);
			return Response.status(Response.Status.OK).entity(result.toString()).build();
		} catch (Exception e) {
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result.toString()).build();
		}
	}
}
