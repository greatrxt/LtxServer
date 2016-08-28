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
import gabriel.utilities.SystemUtils;

public class DriverDao {
	
	public static final String UNREGISTERED_DRIVER = "unregistered_driver";
	public static final String DELETED_DRIVER = "deleted_driver";
	
	/**
	 * Edit driver information in DB
	 * @param username
	 * @param name
	 * @param contactNumber
	 * @param dateOfJoining
	 * @param password
	 * @param image
	 * @return
	 */
	public static JSONObject editDriverInfo(String username, String name, String contactNumber, Date dateOfJoining, String password, String image){
		
		Session session = null;
		
		JSONObject result = new JSONObject();
		
		try {
			
			if(getDriver(username) == null){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Username " + username + " does not exist");
			} else {
			
				session = HibernateUtil.getSessionAnnotationFactory().openSession();
				session.beginTransaction();
				
				Driver driver = getDriver(username);
				driver.setName(name);
				driver.setContactNumber(contactNumber);
				driver.setDateOfJoining(dateOfJoining);
				if(!password.isEmpty()){
					driver.setPassword(password);
				}
				if(image == null){
					driver.setImage(Application.STANDARD_IMAGE_NOT_FOUND);
				} else {
					if(!image.trim().isEmpty()){
						driver.setImage(image);	
					}
				}
				driver.setRecordCreationTime(new Date());	
				
				session.update(driver);		
				
				session.getTransaction().commit();		
				
				result.put(Application.RESULT, Application.SUCCESS);
				
			}
			
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
			result = SystemUtils.generateErrorMessage(e.getMessage());
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
			or.add(Restrictions.ilike("username", "%" + query +"%"));
			or.add(Restrictions.ilike("name", "%" + query + "%"));
			or.add(Restrictions.ilike("contactNumber", "%" + query +"%"));
			criteria.add(or);
			criteria.setMaxResults(5);
			
			List<Driver> drivers = criteria.list();

			JSONArray searchArray = new JSONArray();
			
			Iterator<Driver> iterator = drivers.iterator();
			while(iterator.hasNext()){
				Driver driver = iterator.next();
				
				if(!driver.getUsername().trim().equals(UNREGISTERED_DRIVER) 
						&& !driver.getUsername().trim().equals(DELETED_DRIVER)){
					JSONObject driverJson = new JSONObject();
					driverJson.put("name", driver.getName());
					driverJson.put("username", driver.getUsername());
					driverJson.put("contactNumber", driver.getContactNumber());
					driverJson.put("recordCreationTime", driver.getRecordCreationTime());
					driverJson.put("dateOfJoining", driver.getDateOfJoining());
					driverJson.put("image", driver.getImage());
					searchArray.put(driverJson);
				}
			}
			
			result.put(Application.RESULT, searchArray);
			
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
	 * 
	 * @return
	 */
	public static List<Driver> getAllDriversList(){
		
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);			
			List<Driver> list = criteria.list();
			return list;
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return null;
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
					if(!driver.getUsername().trim().equals(UNREGISTERED_DRIVER) 
							&& !driver.getUsername().trim().equals(DELETED_DRIVER)){
						result.put("name", driver.getName());
						result.put("username", driver.getUsername());
						result.put("contactNumber", driver.getContactNumber());
						//result.put("recordCreationTime", driver.getRecordCreationTime());
						//result.put("dateOfJoining", driver.getDateOfJoining());
						result.put("image", driver.getImage());
						resultArray.put(result);
					}
				}
				resultsJson.put(Application.RESULT, resultArray);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			resultsJson = SystemUtils.generateErrorMessage(e.getMessage());
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return resultsJson;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static JSONObject driverExists(String username, String password){
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);
			criteria.add(Restrictions.eq("username", username));
			criteria.add(Restrictions.eq("password", password));
			List<Driver> list = criteria.list();
			if(list.size() == 1){
				result.put(Application.RESULT, Application.SUCCESS);
			} else {
				result.put(Application.RESULT, Application.FAILED);
			}
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
	 * 
	 * @return unregistered driver
	 */
	public static Driver getDeletedDriver(){
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);
			criteria.add(Restrictions.eq("username", DELETED_DRIVER));
			
			List<Driver> list = criteria.list();
			if(list.size() == 0){
				storeDriverInfo(DELETED_DRIVER, DELETED_DRIVER, "0", new Date(), DELETED_DRIVER, Application.STANDARD_IMAGE_NOT_FOUND);
				return getUnregisteredDriver();
			} else {
				return list.get(0);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @return unregistered driver
	 */
	public static Driver getUnregisteredDriver(){
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);
			criteria.add(Restrictions.eq("username", UNREGISTERED_DRIVER));
			
			List<Driver> list = criteria.list();
			if(list.size() == 0){
				storeDriverInfo(UNREGISTERED_DRIVER, UNREGISTERED_DRIVER, "0", new Date(), UNREGISTERED_DRIVER, Application.STANDARD_IMAGE_NOT_FOUND);
				return getUnregisteredDriver();
			} else {
				return list.get(0);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public static Driver getDriver(String username){
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionAnnotationFactory().openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);
			criteria.add(Restrictions.eq("username", username));
			
			List<Driver> list = criteria.list();
			if(list.size() == 1){
				return list.get(0);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(session!=null){
				session.close();
			}
		}
		
		return null;
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
					JSONObject driverJson = new JSONObject();
					driverJson.put("name", driver.getName());
					driverJson.put("username", driver.getUsername());
					driverJson.put("contactNumber", driver.getContactNumber());
					driverJson.put("recordCreationTime", driver.getRecordCreationTime());
					driverJson.put("dateOfJoining", driver.getDateOfJoining());
					driverJson.put("image", driver.getImage());
					JSONArray driverArray = new JSONArray();
					driverArray.put(driverJson);
					result.put(Application.RESULT, driverArray);
				}
			} else {
				//LOG ERROR HERE ONCE LOGGER IS INTEGRATED
				System.out.println("ERROR. MORE THAN 1 USERS WITH USERNAME :"+username+" FOUND");
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "More than 1 user with Username - " + username + " found");
			}
			
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
	 * Delete driver
	 * @param username
	 * @return
	 */
	public static JSONObject deleteDriver(String username) {
		
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
					result.put(Application.ERROR_MESSAGE, "No driver with username " + username + " found");
				} else {
					Driver driverToDelete = list.get(0);
					boolean locationRecordsUpdated = LocationDao.markDriverAsDeleted(driverToDelete);	//update rows in location table as "driver_deleted" 
					
					if(locationRecordsUpdated){
						session.delete(driverToDelete);
						session.getTransaction().commit();
						result.put(Application.RESULT, Application.SUCCESS);
					} else {
						result = SystemUtils.generateErrorMessage("Failed to delete location records for driver "+driverToDelete.getUsername());
					}
				}
			} else {
				//LOG ERROR HERE ONCE LOGGER IS INTEGRATED
				System.out.println("ERROR. MORE THAN 1 USERS WITH username :"+username+" FOUND");
				result = SystemUtils.generateErrorMessage("More than 1 driver with username "+username + " found");
			}
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
}
