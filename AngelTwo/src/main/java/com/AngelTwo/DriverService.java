package com.AngelTwo;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import javax.servlet.ServletContext;
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
import gabriel.hibernate.dao.DriverDao;
import gabriel.utilities.SystemUtils;  

@Path("/driver")
public class DriverService {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchAllDrivers(@Context ServletContext context){
		JSONObject result = null;
		try {
			result = DriverDao.getAllDrivers();			
			SystemUtils.createDriverImageInTempCache(context, result);			
			return Response.status(200).entity(result.toString()).build();
		} catch(Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(500).entity(result.toString()).build();
		}	
	}
	
	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchDriver(@Context ServletContext context, @PathParam(value = "username") String username){
		JSONObject result = null;
		try {
			result = DriverDao.getDriverInfo(username);			
			SystemUtils.createDriverImageInTempCache(context, result);			
			return Response.status(200).entity(result.toString()).build();
		} catch(Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(500).entity(result.toString()).build();
		}	
	}


	@POST
	@Path("/form")
	public Response uploadFile(InputStream is) {
	
		JSONObject result = new JSONObject();
		try {

			JSONObject inputJson = SystemUtils.convertInputStreamToJSON(is);		
			String missingFields = "";
			String name = null, username = null, password = null, contact, doj, image = null;
			
			if(inputJson.has("name")){
				name = inputJson.getString("name");
				if(name.trim().isEmpty()){
					missingFields+="\nname ";	
				}
			} else {
				missingFields+="\nname ";
			}
			
			if(inputJson.has("username")){
				username = inputJson.getString("username");
				if(username.trim().isEmpty()){
					missingFields+="\nusername ";	
				}
			} else {
				missingFields+="\nusername ";
			}
			
			if(inputJson.has("password")){
				password = inputJson.getString("password");
				if(password.trim().isEmpty()){
					missingFields+="\npassword ";	
				}
			} else {
				missingFields+="\npassword ";
			}

			if(inputJson.has("contact")){
				contact = inputJson.getString("contact");
			} else {
				contact = "";
			}
			
			if(inputJson.has("doj")){
				doj = inputJson.getString("doj");
			} else {
				doj = "";
			}
			
			//process image
			if(inputJson.has("image")){
				image = inputJson.getString("image").split(",")[1];
			} else {
				image = Application.STANDARD_IMAGE_NOT_FOUND.split(",")[1];
			}
			
/*			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image);
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
			File imageFile = new File(imageUploadFolder.getAbsolutePath() + File.separator + username + ".png");
			ImageIO.write(bufferedImage, "png", imageFile);*/
			
			if(missingFields.trim().length() != 0){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Critical Fields Missing : "+missingFields);
				return Response.status(400).entity(result.toString()).build();
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			result = DriverDao.storeDriverInfo(username, name, contact, sdf.parse(doj), password, image);
			
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
}
