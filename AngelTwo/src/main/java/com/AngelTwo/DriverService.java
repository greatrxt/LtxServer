package com.AngelTwo;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import gabriel.application.Application;
import gabriel.utilities.SystemUtils;  
@Path("/driver")
public class DriverService {

	//http://stackoverflow.com/questions/25797650/fileupload-with-jaxrs
	@POST
	@Path("/form")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@Context ServletContext context,
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {

		/**
		 * the name you choose after the FormDataParameter("myForm") has to be the same as the name you choosed in your HTML form (name = "myForm")
		 * Example - 
		 * <form action=".../rest/fileupload" method="post" enctype="multipart/form-data">
			   <p>
			    Select a file : <input type="file" name="myForm"/>
			   </p>
			   <input type="submit" value="Upload It" />
			</form>
		 */
		
		String contextPath = context.getContextPath();
		String contextRealPath = context.getRealPath(contextPath);
		System.out.println(context.getRealPath(contextRealPath));
		String imagePath = contextRealPath + File.separator + Application.FOLDER_UPLOADS + File.separator + Application.FOLDER_DRIVER_IMAGES; 
		File imageUploadFolder = new File(imagePath);
		
		if(!imageUploadFolder.exists()){
			System.out.println("Creating image folder");
			imageUploadFolder.mkdirs();
		}

		// save it
		SystemUtils.writeToFile(uploadedInputStream, imageUploadFolder.getAbsolutePath() + File.separator + "test.png");

		String output = "File uploaded to : " + imageUploadFolder.getAbsolutePath();
		System.out.println(output);
		return Response.status(200).entity(output).build();

	}
}
