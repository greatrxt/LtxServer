package gabriel.hibernate.dao;

import gabriel.application.Application;
import gabriel.hibernate.entity.Driver;
import gabriel.hibernate.entity.Location;
import gabriel.hibernate.entity.Vehicle;
import gabriel.map.Osrm;
import gabriel.utilities.HibernateUtil;
import gabriel.utilities.SystemUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

//http://docs.oracle.com/javaee/6/tutorial/doc/gjivm.html#gjivs
public class LocationDao {
	
	/**
	 * Replaces driver username with a driver "deleted_driver"
	 * @param driverUserName
	 * @return
	 */
	public static boolean markDriverAsDeleted(String team, Driver driverToDelete){
		//run raw query to void delay in case of large number of rows
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			String hql = "UPDATE Location set driver = :deleted_driver "  + 
		             "WHERE driver = :driver";
			Query query = session.createQuery(hql);
			query.setParameter("deleted_driver", DriverDao.getDeletedDriver(team));
			query.setParameter("driver", driverToDelete);
			int result = query.executeUpdate();
			System.out.println("Location ( For Driver ) rows affected: " + result);
			session.getTransaction().commit();
			return true;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
		return false;
	}
	
	
	/**
	 * Replaces vehicle as deleted for location records with vehicle - vehicleToDelete
	 * @param vehicleToDelete
	 * @return
	 */
	public static boolean markVehicleAsDeleted(String team, Vehicle vehicleToDelete){
		//run raw query to void delay in case of large number of rows
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			String hql = "UPDATE Location set vehicle = :deleted_vehicle "  + 
		             "WHERE vehicle = :vehicle";
			Query query = session.createQuery(hql);
			query.setParameter("deleted_vehicle", VehicleDao.getDeletedVehicle(team));
			query.setParameter("vehicle", vehicleToDelete);
			int result = query.executeUpdate();
			System.out.println("Location ( for vehicle ) rows affected: " + result);
			session.getTransaction().commit();
			return true;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
		return false;
	}
	
	/**
	 * Get last known location for every vehicle
	 * @return
	 */
	public static JSONObject getLastKnownLocationForAllVehicles(String team){
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			List<Vehicle> vehiclesList = VehicleDao.getAllVehiclesList(team);
			
			if(vehiclesList == null){
				throw new Exception("Failed to retieve vehicles");
			} else if (vehiclesList.isEmpty()){
				throw new Exception("No vehicles found");
			}
			
			Iterator<Vehicle> iterator = vehiclesList.iterator();
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			JSONArray vehicleArray = new JSONArray();
			while(iterator.hasNext()){
				
				JSONObject vehicleJson = new JSONObject();
				
				Vehicle vehicle = iterator.next();
				Criteria criteria = session.createCriteria(Location.class);
				criteria.add(Restrictions.eq("vehicle", vehicle));
				criteria.add(Restrictions.gt("mBearing", 0.0));
				criteria.add(Restrictions.ne("driver", DriverDao.getDeletedDriver(team)));
				criteria.addOrder(Order.desc("mTime"));
				criteria.setMaxResults(1);
				
				if(criteria.list().size() > 0){
					Location location = (Location) criteria.uniqueResult();
					vehicleJson.put("uniqueId", vehicle.getUniqueId());
					vehicleJson.put("registration", vehicle.getRegistrationNumber());
					
					JSONObject locationJson = new JSONObject();
					if(location!=null){
						locationJson.put("mBearing", location.getmBearing());
						locationJson.put("mLatitude", location.getmLatitude());
						locationJson.put("mLongitude", location.getmLongitude());
						locationJson.put("mTime", location.getmTime());
						locationJson.put("mSpeed", location.getmSpeed());
						locationJson.put("driver", location.getDriver().getUsername());
					} else {
						locationJson.put("bearing", -1);
						locationJson.put("latitude", -1);
						locationJson.put("longitude", -1);
						locationJson.put("mTime", -1);
						locationJson.put("mSpeed", -1);
						locationJson.put("driver", "");
					}
					
					vehicleJson.put("location", locationJson);
					
					vehicleArray.put(vehicleJson);
				} else {
					
				}
			}
			
			result.put(Application.RESULT, vehicleArray);
			
		} catch(Exception e){
			e.printStackTrace();
			result = SystemUtils.generateErrorMessage(e.getMessage());
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return result;
	}

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
	public static String storeLocationPacket(String team, double mLatitude, 
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
		
		Vehicle vehicle = VehicleDao.getVehicle(team, uniqueId);
		Driver driver = DriverDao.getDriver(team, username);
		if(vehicle == null){
			System.out.println("Vehicle with ID "+uniqueId+" NOT found");
			//return "Vehicle with ID " + uniqueId + " NOT found";
			vehicle = VehicleDao.getUnregisteredVehicle(team);
		}
		
		if(driver == null){
			System.out.println("Driver with ursername "+username + " not found");
			//return "Driver with username " + username + " NOT found";
			driver = DriverDao.getUnregisteredDriver(team);
		}
		
		long saveId = -1;
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
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
				Osrm.snapLocation(team, saveId);
			}
		}
	}

	
	/**
	 * Returns raw location records without snapping
	 * @param numOfRecords
	 * @param minAccuracy
	 * @return
	 */
	public static JSONArray getLocationJson(String team, int numOfRecords, int minAccuracy){
		
		JSONArray locationArray = new JSONArray();
		
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
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
		        	
		        	if(snappedLatitude != Osrm.STATUS_SNAP_FAILED && snappedLongitude != Osrm.STATUS_SNAP_FAILED){
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
	public static JSONObject getLocationJsonForVehicle(String team, String vehicleUniqueId, Date fromDate, Date toDate){
		
		JSONObject result = new JSONObject();
		JSONArray locationArray = new JSONArray();
		
		Session session = null;
		try {
			
			Vehicle vehicle = VehicleDao.getVehicle(team, vehicleUniqueId);
			
			if(vehicle==null){
				return SystemUtils.generateErrorMessage("Vehicle with uniqueID "+vehicleUniqueId+" NOT found");
			}
			
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
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
	        	locationJson.put("driver", location.getDriver().getUsername());
	        	
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
		} catch(Exception e){
			result = SystemUtils.generateErrorMessage(e.getMessage());
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return result;
	}
	
}
