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
import gabriel.hibernate.entity.Driver;
import gabriel.utilities.HibernateUtil;

public class DriverDao {
	
	/**
	 * Store driver information in DB
	 * @param username
	 * @param name
	 * @param contactNumber
	 * @param dateOfJoining
	 * @param password
	 * @param image
	 * @return
	 */
	public static JSONObject storeDriverInfo(String username, String name, String contactNumber, Date dateOfJoining, String password, String image){
		
		Session session = null;
		
		JSONObject result = new JSONObject();
		
		try {
			
			if(getDriverInfo(username).has("username")){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Username " + username + " already exists");
			} else {
			
				session = HibernateUtil.getSessionAnnotationFactory().openSession();
				session.beginTransaction();
				
				Driver driver = new Driver();
				driver.setUsername(username);
				driver.setName(name);
				driver.setContactNumber(contactNumber);
				driver.setDateOfJoining(dateOfJoining);
				driver.setPassword(password);
				driver.setImage(image);
				driver.setRecordCreationTime(new Date());	
				
				session.save(driver);		
				
				session.getTransaction().commit();		
				
				result.put(Application.RESULT, Application.SUCCESS);
				
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
	 * Returns search results
	 * @param query
	 * @return
	 */
	public static JSONObject searchDriversFor(String query){
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.ilike("username", query));
			or.add(Restrictions.ilike("name", query));
			or.add(Restrictions.ilike("contact_number", query));
			criteria.add(or);
			
			List<Driver> drivers = criteria.list();
			if(drivers.size() == 0){
				result.put(Application.ERROR, "No users found");
			} else {
				JSONArray searchArray = new JSONArray();
				
				Iterator<Driver> iterator = drivers.iterator();
				while(iterator.hasNext()){
					Driver driver = iterator.next();
					JSONObject driverJson = getDriverInfo(driver.getUsername());
					searchArray.put(driverJson);
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
	 * 
	 * @return
	 */
	public static JSONObject getAllDrivers(){
		
		JSONObject resultsJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);			
			List<Driver> list = criteria.list();

			if(list.size() == 0){
				//No user found
				resultsJson.put(Application.RESULT, Application.ERROR);
				resultsJson.put(Application.ERROR_MESSAGE, "No drivers found");
			} else {
				Iterator<Driver> driverList = list.iterator();
				while(driverList.hasNext()){
					Driver driver = driverList.next();
					JSONObject result = new JSONObject();
					result.put("name", driver.getName());
					result.put("username", driver.getUsername());
					result.put("contactNumber", driver.getContactNumber());
					//result.put("recordCreationTime", driver.getRecordCreationTime());
					//result.put("dateOfJoining", driver.getDateOfJoining());
					result.put("image", driver.getImage());
					resultArray.put(result);
				}
				resultsJson.put(Application.RESULT, resultArray);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			resultsJson = new JSONObject();
			resultsJson.put(Application.RESULT, Application.ERROR);
			resultsJson.put(Application.ERROR_MESSAGE, e.getMessage());
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return resultsJson;
	}
	
	/**
	 * Fetch user with particular username from DB
	 * @param username
	 * @return
	 */
	public static JSONObject getDriverInfo(String username){
	
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);
			criteria.add(Restrictions.eq("username", username));
			
			List<Driver> list = criteria.list();
			if(list.size() <= 1){
				if(list.size() == 0){
					//No user found
					result.put(Application.RESULT, Application.ERROR);
					result.put(Application.ERROR_MESSAGE, "No driver with username - " + username + " found");
				} else {
					Iterator<Driver> driverList = list.iterator();
					Driver driver = driverList.next();
					result.put("name", driver.getName());
					result.put("username", driver.getUsername());
					result.put("contactNumber", driver.getContactNumber());
					result.put("recordCreationTime", driver.getRecordCreationTime());
					result.put("dateOfJoining", driver.getDateOfJoining());
					result.put("image", driver.getImage());
				}
			} else {
				//LOG ERROR HERE ONCE LOGGER IS INTEGRATED
				System.out.println("ERROR. MORE THAN 1 USERS WITH USERNAME :"+username+" FOUND");
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "More than 1 user with Username - " + username + " found");
			}
		} catch(Exception e){
			e.printStackTrace();
			result = new JSONObject();
			result.put(Application.RESULT, Application.ERROR);
			result.put(Application.ERROR_MESSAGE, e.getMessage());
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return result;
	}
}
