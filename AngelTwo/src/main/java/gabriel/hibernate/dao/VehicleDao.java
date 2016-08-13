package gabriel.hibernate.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;

import gabriel.application.Application;
import gabriel.hibernate.entity.Vehicle;
import gabriel.utilities.HibernateUtil;

public class VehicleDao {
	
	/**
	 * Store vehicle information in DB
	 * @param uniqueId
	 * @param name
	 * @param contactNumber
	 * @param dateOfJoining
	 * @param password
	 * @param image
	 * @return
	 */
	public static JSONObject storeVehicleInfo(String uniqueId, String image, String registrationNumber){
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			
			Vehicle vehicle = new Vehicle();
			vehicle.setUniqueId(uniqueId);
			vehicle.setImage(image);
			vehicle.setRegistrationNumber(registrationNumber);
			vehicle.setVehicleCreationTime(new Date());			
			session.save(vehicle);		
			session.getTransaction().commit();
			result.put(Application.RESULT, Application.SUCCESS);
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
	 * Returns search results
	 * @param query
	 * @return
	 */
	public static JSONObject searchVehiclesFor(String query){
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Vehicle.class);
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.ilike("unique_id", query));
			or.add(Restrictions.ilike("registration_number", query));
			criteria.add(or);
			
			List<Vehicle> vehicles = criteria.list();
			if(vehicles.size() == 0){
				result.put(Application.ERROR, "No users found");
			} else {
				JSONArray searchArray = new JSONArray();
				
				Iterator<Vehicle> iterator = vehicles.iterator();
				while(iterator.hasNext()){
					Vehicle vehicle = iterator.next();
					JSONObject vehicleJson = getVehicleInfo(vehicle.getUniqueId());
					searchArray.put(vehicleJson);
				}
				
				result.put(Application.RESULT, searchArray);
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
	 * Fetch user with particular uniqueId from DB
	 * @param uniqueId
	 * @return
	 */
	public static JSONObject getVehicleInfo(String uniqueId){
	
		JSONObject vehicleInfo = new JSONObject();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			
			Criteria criteria = session.createCriteria(Vehicle.class);
			criteria.add(Restrictions.eq("uniqueId", uniqueId));
			
			List<Vehicle> list = criteria.list();
			if(list.size() <= 1){
				if(list.size() == 0){
					//No user found
					vehicleInfo.put(Application.ERROR, "No vehicle with uniqueId " + uniqueId + " found");
				} else {
					Iterator<Vehicle> vehicleList = list.iterator();
					Vehicle vehicle = vehicleList.next();
					vehicleInfo.put("registrationNumber", vehicle.getRegistrationNumber());
					vehicleInfo.put("uniqueId", vehicle.getUniqueId());
					//vehicleInfo.put("vehicleCreationTime", vehicle.getVehicleCreationTime());
					vehicleInfo.put("image", vehicle.getImage());
				}
			} else {
				//LOG ERROR HERE ONCE LOGGER IS INTEGRATED
				System.out.println("ERROR. MORE THAN 1 USERS WITH uniqueId :"+uniqueId+" FOUND");
			}
		} catch(Exception e){
			e.printStackTrace();
			vehicleInfo.put(Application.RESULT, Application.ERROR);
			vehicleInfo.put(Application.ERROR, e.getMessage());
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return vehicleInfo;
	}
}
