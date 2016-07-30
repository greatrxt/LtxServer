package gabriel.hibernate.dao;

import java.util.Base64;
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
	public static String storeDriverInfo(String username, String name, String contactNumber, Date dateOfJoining, String password, byte[] image){
		
		Session session = HibernateUtil.getSessionAnnotationFactory().openSession();
		
		try {		
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
			
		} catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		} finally {
			session.close();
		}
		
		return Application.SUCCESS;
	}

	/**
	 * Returns search results
	 * @param query
	 * @return
	 */
	public static JSONObject searchDriversFor(String query){
		JSONObject result = new JSONObject();
		Session session = HibernateUtil.getSessionAnnotationFactory().openSession();
		try {
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
			result.put(Application.ERROR, e.getMessage());
		} finally {
			session.close();
		}
		
		return result;
	}
	
	/**
	 * Fetch user with particular username from DB
	 * @param username
	 * @return
	 */
	public static JSONObject getDriverInfo(String username){
	
		JSONObject driverInfo = new JSONObject();
		Session session = HibernateUtil.getSessionAnnotationFactory().openSession();
		try {
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(Driver.class);
			criteria.add(Restrictions.eq("username", username));
			
			List<Driver> list = criteria.list();
			if(list.size() <= 1){
				if(list.size() == 0){
					//No user found
					driverInfo.put(Application.ERROR, "No driver with username - " + username + " found");
				} else {
					Iterator<Driver> driverList = list.iterator();
					Driver driver = driverList.next();
					driverInfo.put("name", driver.getName());
					driverInfo.put("username", driver.getUsername());
					driverInfo.put("contactNumber", driver.getContactNumber());
					driverInfo.put("recordCreationTime", driver.getRecordCreationTime());
					driverInfo.put("dateOfJoining", driver.getDateOfJoining());
					driverInfo.put("image", Base64.getEncoder().encodeToString(driver.getImage()));
				}
			} else {
				//LOG ERROR HERE ONCE LOGGER IS INTEGRATED
				System.out.println("ERROR. MORE THAN 1 USERS WITH USERNAME :"+username+" FOUND");
			}
		} catch(Exception e){
			e.printStackTrace();
			driverInfo.put(Application.ERROR, e.getMessage());
		} finally {
			session.close();
		}
		
		return driverInfo;
	}
}
