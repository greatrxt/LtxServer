package com.AngelTwo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
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
			
			if(!result.get("result").toString().trim().equals(Application.ERROR)){
				//Create a copy of image on server that can be accessed from the web client
				String contextPath = context.getContextPath();
				String contextRealPath = context.getRealPath(contextPath);
				System.out.println(context.getRealPath(contextRealPath));
				String imagePath = contextRealPath + File.separator + Application.FOLDER_UPLOADS + File.separator + Application.FOLDER_DRIVER_IMAGES; 
				File imageUploadFolder = new File(imagePath);
				
				if(!imageUploadFolder.exists()){
					System.out.println("Creating image folder");
					imageUploadFolder.mkdirs();
				}
				
				JSONArray driverArray = result.getJSONArray("result");
				for(int d = 0; d < driverArray.length(); d++){
					JSONObject driver = driverArray.getJSONObject(d);
					String image = Application.STANDARD_IMAGE_NOT_FOUND;
					if(driver.has("image")){
						if(!driver.getString("image").isEmpty()){
							image = driver.getString("image");
						}
					}
					
					byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image);
					BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
					File imageFile = new File(imageUploadFolder.getAbsolutePath() + File.separator + driver.getString("username") + ".png");
					ImageIO.write(bufferedImage, "png", imageFile);
				}	
			}
			
			return Response.status(200).entity(result.toString()).build();
			
		} catch(Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
			return Response.status(500).entity(result.toString()).build();
		}	
	}

	//http://stackoverflow.com/questions/25797650/fileupload-with-jaxrs
	//http://stackoverflow.com/questions/17710147/image-convert-to-base64
	@POST
	@Path("/form")
	public Response uploadFile(@Context ServletContext context, InputStream is) {
	
		JSONObject result = new JSONObject();
		try {
			String contextPath = context.getContextPath();
			String contextRealPath = context.getRealPath(contextPath);
			System.out.println(context.getRealPath(contextRealPath));
			String imagePath = contextRealPath + File.separator + Application.FOLDER_UPLOADS + File.separator + Application.FOLDER_DRIVER_IMAGES; 
			File imageUploadFolder = new File(imagePath);
			
			if(!imageUploadFolder.exists()){
				System.out.println("Creating image folder");
				imageUploadFolder.mkdirs();
			}

			JSONObject inputJson = SystemUtils.convertInputStreamToJSON(is);		
		

			/**
			 *    	driver.name = document.getElementById('driver-name').value;
				    driver.username = document.getElementById('driver-username').value;
				    driver.password = document.getElementById('driver-password').value;
				    driver.contact = document.getElementById('driver-contact').value;
				    driver.doj = document.getElementById('driver-doj').value;
				    driver.image = driverImageInBase64;
			 */
		
			String name, username, password, contact, doj, image = null;
			
			if(inputJson.has("name")){
				name = inputJson.getString("name");
			} else {
				name = "";
			}
			
			if(inputJson.has("username")){
				username = inputJson.getString("username");
			} else {
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Critical field missing : Username");
				return Response.status(400).entity(result.toString()).build();
			}
			
			if(inputJson.has("password")){
				password = inputJson.getString("password");
			} else {
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Critical field missing : Password");
				return Response.status(400).entity(result.toString()).build();
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
			
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image);
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
			File imageFile = new File(imageUploadFolder.getAbsolutePath() + File.separator + username + ".png");
			ImageIO.write(bufferedImage, "png", imageFile);
			
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
