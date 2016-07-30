package gabriel.hibernate.dao;

import gabriel.application.Application;
import gabriel.hibernate.entity.Location;
import gabriel.utilities.HibernateUtil;
import gabriel.utilities.SystemUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.json.JSONArray;
import org.json.JSONObject;

import gabirel.map.Osrm;

public class LocationDao {

	/**
	 * Store location packets received from device
	 * @param mLatitude
	 * @param mLongitude
	 * @param mAccuracy
	 * @param mSpeed
	 * @param mDistance
	 * @param mAltitude
	 * @param mTime
	 * @param mBearing
	 * @return
	 */
	public static String storeLocationPacket(double mLatitude, 
									double mLongitude, 
									double mAccuracy, 
									double mSpeed, 
									double mDistance, 
									double mAltitude, 
									long mTime, 
									double mBearing){
		
		Session session = HibernateUtil.getSessionAnnotationFactory().openSession();
		long saveId = -1;
		try {
			session.beginTransaction();
			Location location = new Location();
			location.setmAccuracy(mAccuracy);
			location.setmAltitude(mAltitude);
			location.setmBearing(mBearing);
			location.setmDistance(mDistance);
			location.setmLatitude(mLatitude);
			location.setmLongitude(mLongitude);
			location.setSnappedLatitude(Osrm.STATUS_SNAP_PENDING);
			location.setSnappedLongitude(Osrm.STATUS_SNAP_PENDING);
			location.setmSpeed(mSpeed);
			SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date mLocationTime = null;
			try {
				mLocationTime = parserSDF.parse(SystemUtils.convertMsSinceEpochToDate(mTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			location.setmTime(mLocationTime);
			location.setPacketReceivedTime(new Date());
			session.save(location);
			
			//http://stackoverflow.com/questions/25561681/hibernate-get-id-after-save-object
			saveId = location.getId();
			session.getTransaction().commit();
			return Application.SUCCESS;
		} catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		} finally {
			session.close();
			if(saveId!=-1){
				Osrm.snapLocation(saveId);
			}
		}
	}

	
	/**
	 * Returns raw location records without snapping
	 * @param numOfRecords
	 * @param minAccuracy
	 * @return
	 */
	public static JSONArray getLocationJson(int numOfRecords, int minAccuracy){
		
		JSONArray locationArray = new JSONArray();
		
		Session session = HibernateUtil.getSessionAnnotationFactory().openSession();
		try {
			session.beginTransaction();
			Criteria count = session.createCriteria(Location.class);
	        count.setProjection(Projections.rowCount());
	        long total = (long) count.uniqueResult();
	        if(total < numOfRecords){
	        	numOfRecords = (int) total;
	        }
	        Criteria criteria = session.createCriteria(Location.class);
	        criteria.setFirstResult((int) (total - numOfRecords));
	        criteria.addOrder(Order.asc("mTime"));	//VERY VERY IMPORTANT !
	        criteria.setMaxResults(numOfRecords);

	        List<Location> list = criteria.list();
	        Iterator<Location> locations = list.iterator();
	       
	        while(locations.hasNext()){
	        	Location location = locations.next();
	        	JSONObject locationJson = new JSONObject();
	        	
	        	if(location.getmAccuracy() < minAccuracy){     		
		        	locationJson.put("mAccuracy", location.getmAccuracy());
		        	locationJson.put("mSpeed", location.getmSpeed());
		        	locationJson.put("mTime", location.getmTime());
		        	locationJson.put("mBearing", location.getmBearing());
		        	locationJson.put("mLatitude", location.getmLatitude());
		        	locationJson.put("mLongitude", location.getmLongitude());    
		        	locationJson.put("snappedLatitude", location.getSnappedLatitude());
		        	locationJson.put("snappedLongitude", location.getSnappedLongitude());  
		        	locationArray.put(locationJson);
		        	
	        	}
	        }
	        return locationArray;
		} finally {
			session.close();
		}
	}
	
}
