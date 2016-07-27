package gabriel.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import gabriel.hibernate.dao.LocationDao;
import gabriel.hibernate.entity.Location;

public class SystemUtils {
	

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
			
			System.out.println(responseStrBuilder.toString());
			return new JSONObject(responseStrBuilder.toString().trim());
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
