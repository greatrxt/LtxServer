package gabriel.utilities;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;

import gabriel.application.Application;

public class SystemUtils {
	
	/**
	 * Used to generate error message
	 * @param errorMessage
	 * @return
	 */
	public static JSONObject generateErrorMessage(String errorMessage){
		JSONObject error = new JSONObject();
		error.put(Application.RESULT, Application.ERROR);
		error.put(Application.ERROR_MESSAGE, errorMessage);
		return error;
	}
	
	/**
	 * save uploaded file to new location
	 * @param uploadedInputStream
	 * @param uploadedFileLocation
	 */
	public static void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			int size = 0;
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
				size+=read;
			}
			System.out.println("Wrote "+ size+" bytes");
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
/*	
	public static void writeImageToFile(InputStream uploadedInputStream,
			String uploadedFileLocation){
		try {
			BufferedImage bufferedImage = ImageIO.read(uploadedInputStream);
			ImageIO.write(bufferedImage, "jpg", new File(uploadedFileLocation));
			
			byte[] bytes = convertInputStreamToByteArray(uploadedInputStream);
			System.out.println("Data size -- >"+bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/

	/**
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] convertInputStreamToByteArray(InputStream is) throws IOException{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}
	/**
	 * 
	 * @param is
	 * @return
	 */
	public static JSONObject convertInputStreamToJSON(InputStream is){
		BufferedReader streamReader;
		try {
			streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();

			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
			    responseStrBuilder.append(inputStr);
			
			System.out.println("--------------Received JSON------------ \n\n\n"+responseStrBuilder.toString()+"\n\n-------------------------\n\n");
			return new JSONObject(responseStrBuilder.toString().trim());
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	
	/**
	 * 
	 * @param is
	 * @return
	 */
	public static String convertInputStreamToString(InputStream is){
		BufferedReader streamReader;
		try {
			streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();

			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
			    responseStrBuilder.append(inputStr);
			
			System.out.println("--------------Received------------ \n\n\n"+responseStrBuilder.toString()+"\n\n-------------------------\n\n");
			return responseStrBuilder.toString().trim();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * Convert epoch time to human readable format
	 * @param msSinceEpoch
	 * @return
	 */
	public static String convertMsSinceEpochToDate(long msSinceEpoch){
		Date date = new Date(msSinceEpoch);
        //DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        //format.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
        //formatted = format.format(date);
        return formatted;
	}

	/**
	 * Creates image in driver folder
	 * @param context
	 * @param result
	 * @throws IOException
	 */
	public static void createUserImageInTempCache(String team, ServletContext context, JSONObject result) throws IOException {
		if(!result.get("result").toString().trim().equals(Application.ERROR)){
			//Create a copy of image on server that can be accessed from the web client
			String contextPath = context.getContextPath();
			String contextRealPath = context.getRealPath(contextPath);
			
			contextRealPath = contextRealPath.replace("\\AngelTwo\\AngelTwo", "") + File.separator + "\\ROOT\\AngelTwoTempCache";
			
			String imagePath = contextRealPath + File.separator + team + File.separator + Application.FOLDER_UPLOADS + File.separator + Application.FOLDER_USER_IMAGES; 
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
						driver.remove("image");	//remove image. 
					}
				}
				
				byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image);
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
				File imageFile = new File(imageUploadFolder.getAbsolutePath() + File.separator + driver.getString("username") + ".png");
				
				if(imageFile.exists()){
					imageFile.delete();
				}
				
				if(bufferedImage!=null && imageFile!=null){
					ImageIO.write(bufferedImage, "png", imageFile);
					driver.put("image", imageFile.getAbsolutePath().split("ROOT")[1]);
				}
			}	
		}
		
	}
	
	public static void createVehicleImageInTempCache(String team, ServletContext context, JSONObject result) throws IOException {
		if(!result.get("result").toString().trim().equals(Application.ERROR)){
			//Create a copy of image on server that can be accessed from the web client
			String contextPath = context.getContextPath();
			String contextRealPath = context.getRealPath(contextPath);
			
			contextRealPath = contextRealPath.replace("\\AngelTwo\\AngelTwo", "") + File.separator + "\\ROOT\\AngelTwoTempCache";

			
			System.out.println(context.getRealPath(contextRealPath));
			String imagePath = contextRealPath + File.separator + team + File.separator + Application.FOLDER_UPLOADS + File.separator + Application.FOLDER_VEHICLE_IMAGES; 
			File imageUploadFolder = new File(imagePath);
			
			if(!imageUploadFolder.exists()){
				System.out.println("Creating image folder");
				imageUploadFolder.mkdirs();
			}
			
			JSONArray vehicleArray = result.getJSONArray("result");
			for(int d = 0; d < vehicleArray.length(); d++){
				JSONObject vehicle = vehicleArray.getJSONObject(d);
				String image = Application.STANDARD_IMAGE_NOT_FOUND;
				if(vehicle.has("image")){
					if(!vehicle.getString("image").isEmpty()){
						image = vehicle.getString("image");
						vehicle.remove("image");	//remove image. 
					}
				}
				
				byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image);
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
				File imageFile = new File(imageUploadFolder.getAbsolutePath() + File.separator + vehicle.getString("uniqueId") + ".png");
				
				if(imageFile.exists()){
					imageFile.delete();
				}
				
				
				if(bufferedImage!=null && imageFile!=null){
					ImageIO.write(bufferedImage, "png", imageFile);
					vehicle.put("image", imageFile.getAbsolutePath().split("ROOT")[1]);
				}
			}	
		}
		
	}
}
