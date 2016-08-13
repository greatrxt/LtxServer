package com.AngelTwo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
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

import org.json.JSONObject;

import gabriel.application.Application;
import gabriel.hibernate.dao.VehicleDao;
import gabriel.utilities.SystemUtils;

@Path("vehicle")
public class VehicleService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/form")
	public static Response createVehicle(InputStream is, @Context ServletContext context){
	
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
			} else {
				missingFields+="uniqueId ";
			}
			
			if(inputJson.has("registrationNumber")){
				registrationNumber = inputJson.getString("registrationNumber");
			} else {
				missingFields+="registrationNumber";
			}
			
			if(missingFields.trim().length() != 0){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Critical fields missing : "+missingFields);
				return Response.status(400).entity(result.toString()).build();
			}
			
			//process image
			if(inputJson.has("image")){
				image = inputJson.getString("image").split(",")[1];
			} else {
				image = Application.STANDARD_IMAGE_NOT_FOUND.split(",")[1];
			}
			
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image);
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
			File imageFile = new File(imageUploadFolder.getAbsolutePath() + File.separator + uniqueId + ".png");
			ImageIO.write(bufferedImage, "png", imageFile);
			
			result = VehicleDao.storeVehicleInfo(uniqueId, image, registrationNumber);
			
			if(result.getString(Application.RESULT).equals(Application.SUCCESS)){
				return Response.status(200).entity(result.toString()).build();	
			} else {
				return Response.status(409).entity(result.toString()).build();
			}
		
		} catch(Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(500).entity(result.toString()).build();
		}		
		
	}
	
	
	@Path("/info/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public static Response getVehicleInfo(@PathParam("id") String uniqueId){
		JSONObject vehicleInfo = VehicleDao.getVehicleInfo(String.valueOf(uniqueId));
		return Response.status(Response.Status.OK).entity(vehicleInfo.toString()).build();
	}
}
