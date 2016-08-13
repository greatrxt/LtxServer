package gabriel.hibernate.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.json.JSONObject;

import gabriel.application.Application;
import gabriel.hibernate.entity.Driver;
import gabriel.hibernate.entity.Trip;
import gabriel.hibernate.entity.Vehicle;
import gabriel.utilities.HibernateUtil;

public class TripDao {

	/**
	 * Gets updated everytime location is received from Vehicle
	 * Check if Vehicle - Driver - Date ( with isTripOver- false) row exists
	 *
	 * if YES, update lastKnownLatitude, lastKnowLongitude and lastKnownTime
	 *	
	 * if NO, check last record for Vehicle -> a) If isTripOver == false, make it true b) if isTripOver == true, do nothing
	 * Create new row with V- D -D values and isTripOver == false. Put current time as startTime and lastKnownTime. Put latitude longitude as startLatitude,
	 * startLongitude, lastKnownLatitude and lastKnownLongitude
	 *	
	 * @param vehicleUniqueId
	 * @param driverUsername
	 * @param recordDate
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	//http://stackoverflow.com/questions/1787086/hibernate-query-a-foreign-key-field-with-id
	//http://stackoverflow.com/questions/18053712/hibernate-criteria-filter-by-foreign-key
	public static JSONObject saveTripData(String vehicleUniqueId, String driverUsername, Date recordDate, double latitude, double longitude){
		
		JSONObject result = new JSONObject();
		Session session = null;
		
		try {
			
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
		
			session.beginTransaction();
			Criteria criteria = session.createCriteria(Trip.class);
			
			Criteria criteriaForVehicle = session.createCriteria(Vehicle.class);
			criteriaForVehicle.add(Restrictions.eq("uniqueId", vehicleUniqueId)); //uniqueId and NOT unique_id
			List<Vehicle> listVehicles = criteriaForVehicle.list();
			Vehicle vehicle = null;
			if(listVehicles.size() == 1){
				vehicle = listVehicles.iterator().next();
			}
			
			Criteria criteriaForDriver = session.createCriteria(Driver.class);
			criteriaForDriver.add(Restrictions.eq("username", driverUsername));
			
			List<Driver> listDrivers = criteriaForDriver.list();
			Driver driver = null;
			if(listDrivers.size() == 1){
				driver = listDrivers.iterator().next();
			}
			
			if(vehicle!=null && driver!=null){
				Conjunction and = Restrictions.conjunction();
				and.add(Restrictions.eq("vehicle", vehicle));
				and.add(Restrictions.eq("driver", driver));
							
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YYYY");
				String inputString = formatter.format(recordDate);
				Date date = formatter.parse(inputString);
				
				and.add(Restrictions.eq("tripDate", date));
				and.add(Restrictions.eq("tripOver", false));
				
				criteria.add(and);
			}
			
/*			ClassMetadata classMetadata = HibernateUtil.getSessionAnnotationFactory().getClassMetadata(Trip.class);
			String[] propertyNames = classMetadata.getPropertyNames();*/
			
			List<Trip> trips = criteria.list();
			if(trips.size() == 0){
				endAllEarlierTripsForVehicle(vehicleUniqueId);
				createTrip(vehicleUniqueId, driverUsername, recordDate, latitude, longitude);
				result.put(Application.RESULT, Application.SUCCESS);
			} else if (trips.size() == 1){
				Iterator<Trip> tripIterator = trips.iterator();
				Trip trip = tripIterator.next();
				updateTrip(trip, recordDate, latitude, longitude);
				result.put(Application.RESULT, Application.SUCCESS);
			} else {
				//size > 1. Something wrong !
				System.out.println("ERROR ! MORE THAN 1 TRIP FOR VEHICLE - "+vehicleUniqueId+" FOUND");
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "More than 1 trip for vehicle "+vehicleUniqueId+" found");
			}
		} catch(Exception e){
			e.printStackTrace();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @param vehicleUniqueId
	 */
	private static void endAllEarlierTripsForVehicle(String vehicleUniqueId) {
		System.out.println("Ending all earlier trips for "+vehicleUniqueId);
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Trip.class);
			criteria.add(Restrictions.eq("tripOver", false));
			List<Trip> trips = criteria.list();
			for(Trip trip : trips){
				trip.setTripOver(true);
				session.update(trip);
			}
			session.getTransaction().commit();
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
	}

	/**
	 * Update existing trip record
	 * @param trip
	 * @param recordDate
	 * @param latitude
	 * @param longitude
	 */
	private static void updateTrip(Trip trip, Date recordDate, double latitude, double longitude) {
		System.out.println("Updating trip");
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			trip.setLastKnownLatitude(latitude);
			trip.setLastKnownLongitude(longitude);
			trip.setLastKnownTime(recordDate);
			session.update(trip);
			session.getTransaction().commit();
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
	}

	/**
	 * Create new trip record
	 * @param vehicleUniqueId
	 * @param driverUsername
	 * @param recordDate
	 * @param latitude
	 * @param longitude
	 */
	private static void createTrip(String vehicleUniqueId, String driverUsername, Date recordDate, double latitude,
			double longitude) {
		System.out.println("Creating trip");
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteriaForVehicle = session.createCriteria(Vehicle.class);
			criteriaForVehicle.add(Restrictions.eq("uniqueId", vehicleUniqueId)); //uniqueId and NOT unique_id
			List<Vehicle> listVehicles = criteriaForVehicle.list();
			Vehicle vehicle = null;
			if(listVehicles.size() == 1){
				vehicle = listVehicles.iterator().next();
			} else {
				System.out.println("Vehicle NOT found");
			}
			
			Criteria criteriaForDriver = session.createCriteria(Driver.class);
			criteriaForDriver.add(Restrictions.eq("username", driverUsername));
			
			List<Driver> listDrivers = criteriaForDriver.list();
			Driver driver = null;
			if(listDrivers.size() == 1){
				driver = listDrivers.iterator().next();
			} else {
				System.out.println("Driver NOT found");
			}
			
			if(vehicle!=null && driver!=null){
				Trip trip = new Trip();
				trip.setDriver(driver);
				trip.setVehicle(vehicle);
				trip.setStartLatitude(latitude);
				trip.setStartLongitude(longitude);
				trip.setLastKnownLatitude(latitude);
				trip.setLastKnownLongitude(longitude);
				trip.setTripOver(false);

				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YYYY");
				String inputString = formatter.format(recordDate);
				Date timeTruncatedDate = formatter.parse(inputString);
				
				trip.setTripDate(timeTruncatedDate);
				trip.setStartTime(recordDate);
				trip.setLastKnownTime(recordDate);
				
				session.save(trip);
				
				session.getTransaction().commit();
			} 
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
	}
	
}
