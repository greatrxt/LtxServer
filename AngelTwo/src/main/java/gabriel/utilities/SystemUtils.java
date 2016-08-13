package gabriel.utilities;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
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

import org.json.JSONObject;

public class SystemUtils {
	
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
}
