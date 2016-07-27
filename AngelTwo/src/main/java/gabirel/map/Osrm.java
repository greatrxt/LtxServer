package gabirel.map;

import java.util.Timer;
import java.util.TimerTask;

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import gabriel.hibernate.entity.Location;
import gabriel.utilities.HibernateUtil;
import gabriel.utilities.HttpUtils;

//https://www.digitalocean.com/community/tutorials/how-to-set-up-an-osrm-server-on-ubuntu-14-04

//https://github.com/Project-OSRM/osrm-backend/blob/master/docs/http.md
	
public class Osrm {

	public static final int STATUS_SNAP_PENDING = -1,
							STATUS_SNAP_FAILED = -2;
	
	private static final String JSON_LABEL_WAYPOINTS = "waypoints",
								JSON_LABEL_LOCATION = "location";
	private static final String OSRM_URL = "http://127.0.0.1:5000/nearest/v1/driving/";
	/**
	 * Adds snapped lat lon value to location table
	 */
	public static void snapLocation(final int recordId){
		new Thread(new Runnable() {
//		new Timer().schedule(
	//	new TimerTask() {
			@Override
			public void run() {
				Session session = HibernateUtil.getSessionAnnotationFactory().openSession();
				session.beginTransaction();
				try {
				
					Location location = (Location) session.get(Location.class, recordId);
					location.setSnappedLatitude(STATUS_SNAP_FAILED);
					location.setSnappedLongitude(STATUS_SNAP_FAILED);
					session.update(location);
					
					double rawLatitude = location.getmLatitude();
					double rawLongitude = location.getmLongitude();
					System.out.print("Snapping "+recordId+" - "+rawLatitude+", "+rawLongitude);
					if(rawLatitude > 0 && rawLongitude > 0){
						String url = OSRM_URL + rawLongitude +","+rawLatitude;
						String response = HttpUtils.doGetToFetchString(url);
					
						JSONObject responseJson = new JSONObject(response);
						JSONArray wayPointsArray = responseJson.getJSONArray(JSON_LABEL_WAYPOINTS);
						
						if(wayPointsArray.length() > 0){
							JSONObject wayPoint = wayPointsArray.getJSONObject(0);
							JSONArray locationArray = wayPoint.getJSONArray(JSON_LABEL_LOCATION);
							double snappedLatitude = locationArray.getDouble(1);
							double snappedLongitude = locationArray.getDouble(0);
							location.setSnappedLatitude(snappedLatitude);
							location.setSnappedLongitude(snappedLongitude);
							session.update(location);
							System.out.println(" to "+snappedLatitude+", "+snappedLongitude);
						}
					}
					
				} catch(Exception e){
					e.printStackTrace();
				} finally {
					session.getTransaction().commit();	// commit in finally to save STATUS_SNAP_FAILED in case of error
					session.close();
				}
			}
		}).start();
		//}, 2000);
	}
	
	/**
	 * Adds snapped lat lon to ping table
	 */
	public static void snapPing(int recordId){
		
	}
	
	/**
	 * Makes request to OSRM server running on port 5000 and fetches snapped lat lon
	 * @param location
	 * @return
	 */
	private static Location snapLocation(Location location){
		return null;
	}
}
