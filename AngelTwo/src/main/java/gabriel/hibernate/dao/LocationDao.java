package gabriel.hibernate.dao;

import gabriel.application.Application;
import gabriel.hibernate.entity.Driver;
import gabriel.hibernate.entity.Location;
import gabriel.hibernate.entity.Vehicle;
import gabriel.map.Osrm;
import gabriel.utilities.HibernateUtil;
import gabriel.utilities.SystemUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;

//http://docs.oracle.com/javaee/6/tutorial/doc/gjivm.html#gjivs
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
									double mBearing,
									int signalStrength,
									double batteryCharge,
									String uniqueId,
									String username,
									String rawPacket){
		
		Vehicle vehicle = VehicleDao.getVehicle(uniqueId);
		Driver driver = DriverDao.getDriver(username);
		if(vehicle == null){
			System.out.println("Vehicle with ID "+uniqueId+" NOT found");
			return "Vehicle with ID " + uniqueId + " NOT found";
		}
		
		if(driver == null){
			System.out.println("Driver with ursername "+username + " not found");
			return "Driver with username " + username + " NOT found";
		}
		
		long saveId = -1;
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
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
			location.setSignalStrength(signalStrength);
			location.setVehicle(vehicle);
			location.setDriver(driver);
			location.setBatteryCharge(batteryCharge);
			location.setRawPacket(rawPacket);
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
			if(session!=null){
				session.close();
			}
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
		
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
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
		        	
		        	double snappedLatitude = location.getSnappedLatitude();
		        	double snappedLongitude = location.getSnappedLongitude();
		        	
		        	if(snappedLatitude != Osrm.STATUS_SNAP_FAILED && snappedLongitude!=Osrm.STATUS_SNAP_FAILED){
			        	locationJson.put("snappedLatitude", location.getSnappedLatitude());
			        	locationJson.put("snappedLongitude", location.getSnappedLongitude());  
		        	} else {
		        		//Osrm.snapLocation(location.getId());
		        	}
		        	locationArray.put(locationJson);
		        	
	        	}
	        }
	        return locationArray;
		} finally {
			if(session!=null){
				session.close();
			}
		}
	}
	
	/**
	 * Fetch location for Vehicle between certain dates
	 * @param vehicleUniqueId
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static JSONObject getLocationJsonForVehicle(String vehicleUniqueId, Date fromDate, Date toDate){
		
		JSONObject result = new JSONObject();
		JSONArray locationArray = new JSONArray();
		
		Session session = null;
		try {
			
			Vehicle vehicle = VehicleDao.getVehicle(vehicleUniqueId);
			
			if(vehicle==null){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Vehicle with ID + " + vehicleUniqueId + " NOT found");
				return result;
			}
			
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			Criteria count = session.createCriteria(Location.class);
	        count.setProjection(Projections.rowCount());
	       
	        Criteria criteria = session.createCriteria(Location.class);
	        criteria.addOrder(Order.asc("mTime"));	//VERY VERY IMPORTANT !
/*	        if(fromDate.compareTo(toDate) != 0){
	        	criteria.add(Restrictions.ge("mTime", fromDate));
	        	criteria.add(Restrictions.le("mTime", toDate));
	        } else {*/
        	criteria.add(Restrictions.ge("mTime", fromDate));
        	criteria.add(Restrictions.eq("vehicle", vehicle));
        	
        	Calendar c = Calendar.getInstance(); 
        	c.setTime(toDate); 
        	c.add(Calendar.DATE, 1);
        	toDate = c.getTime();
        	
        	criteria.add(Restrictions.le("mTime", toDate));
	        //}
	        
	        List<Location> list = criteria.list();
	        Iterator<Location> locations = list.iterator();
	       
	        while(locations.hasNext()){
	        	Location location = locations.next();
	        	JSONObject locationJson = new JSONObject();	
	        	
	        	locationJson.put("mAccuracy", location.getmAccuracy());
	        	locationJson.put("mSpeed", location.getmSpeed());
	        	locationJson.put("mTime", location.getmTime());
	        	locationJson.put("mBearing", location.getmBearing());
	        	locationJson.put("mLatitude", location.getmLatitude());
	        	locationJson.put("mLongitude", location.getmLongitude());    
	        	
	        	double snappedLatitude = location.getSnappedLatitude();
	        	double snappedLongitude = location.getSnappedLongitude();
	        	
	        	if(snappedLatitude != Osrm.STATUS_SNAP_FAILED && snappedLongitude!=Osrm.STATUS_SNAP_FAILED){
		        	locationJson.put("snappedLatitude", location.getSnappedLatitude());
		        	locationJson.put("snappedLongitude", location.getSnappedLongitude());  
	        	} else {
	        		//Osrm.snapLocation(location.getId());
	        	}
	        	locationArray.put(locationJson);
	        }
	        
	        result.put(Application.RESULT, locationArray);
	        return result;
		} finally {
			if(session!=null){
				session.close();
			}
		}
	}
	
}
