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
import gabriel.hibernate.entity.User;
import gabriel.utilities.HibernateUtil;
import gabriel.utilities.SystemUtils;

public class UserDao {
	
	public static final String DELETED_USER = "deleted_user";
	
	/**
	 * Edit user information in DB
	 * @param username
	 * @param name
	 * @param contactNumber
	 * @param dateOfJoining
	 * @param password
	 * @param image
	 * @return
	 */
	public static JSONObject editUserInfo(String team, String username, String name, String contactNumber, String password, String image){
		
		Session session = null;
		
		JSONObject result = new JSONObject();
		
		try {
			
			if(getUser(team, username) == null){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Username " + username + " does not exist");
			} else {
			
				session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
				session.beginTransaction();
				
				User user = getUser(team, username);
				user.setName(name);
				user.setContactNumber(contactNumber);
				if(!password.isEmpty()){
					user.setPassword(password);
				}
				if(image == null){
					user.setImage(Application.STANDARD_IMAGE_NOT_FOUND);
				} else {
					if(!image.trim().isEmpty()){
						user.setImage(image);	
					}
				}
				user.setRecordCreationTime(new Date());	
				
				session.update(user);		
				
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
	 * Store user information in DB
	 * @param username
	 * @param name
	 * @param contactNumber
	 * @param dateOfJoining
	 * @param password
	 * @param image
	 * @return
	 */
	public static JSONObject storeUserInfo(String team, String username, String name, String contactNumber, String password, String image){
		
		Session session = null;
		
		JSONObject result = new JSONObject();
		
		try {
			
			if(getUserInfo(team, username).has("username")){
				result.put(Application.RESULT, Application.ERROR);
				result.put(Application.ERROR_MESSAGE, "Username " + username + " already exists");
			} else {
			
				session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
				session.beginTransaction();
				
				User user = new User();
				user.setUsername(username);
				user.setName(name);
				user.setContactNumber(contactNumber);
				user.setPassword(password);
				user.setImage(image);
				user.setRecordCreationTime(new Date());	
				
				session.save(user);		
				
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
	public static JSONObject searchUsersFor(String team, String query){
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(User.class);
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.ilike("username", "%" + query +"%"));
			or.add(Restrictions.ilike("name", "%" + query + "%"));
			or.add(Restrictions.ilike("contactNumber", "%" + query +"%"));
			criteria.add(or);
			criteria.setMaxResults(5);
			
			List<User> users = criteria.list();

			JSONArray searchArray = new JSONArray();
			
			Iterator<User> iterator = users.iterator();
			while(iterator.hasNext()){
				User user = iterator.next();
				
				if(!user.getUsername().trim().equals(DELETED_USER)){
					JSONObject userJson = new JSONObject();
					userJson.put("name", user.getName());
					userJson.put("username", user.getUsername());
					userJson.put("contactNumber", user.getContactNumber());
					userJson.put("recordCreationTime", user.getRecordCreationTime());
					userJson.put("image", user.getImage());
					searchArray.put(userJson);
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
	public static List<User> getAllUsersList(String team){
		
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(User.class);			
			List<User> list = criteria.list();
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
	 * @param team 
	 * @return
	 */
	public static JSONObject getAllUsers(String team){
		
		JSONObject resultsJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(User.class);			
			List<User> list = criteria.list();

			if(list.size() == 0){
				//No user found
				resultsJson.put(Application.RESULT, Application.ERROR);
				resultsJson.put(Application.ERROR_MESSAGE, "No users found");
			} else {
				Iterator<User> userList = list.iterator();
				while(userList.hasNext()){
					User user = userList.next();
					JSONObject result = new JSONObject();
					if(!user.getUsername().trim().equals(DELETED_USER)){
						result.put("name", user.getName());
						result.put("username", user.getUsername());
						result.put("contactNumber", user.getContactNumber());
						//result.put("recordCreationTime", user.getRecordCreationTime());
						//result.put("dateOfJoining", user.getDateOfJoining());
						result.put("image", user.getImage());
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
	public static JSONObject userExists(String team, String username, String password){
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("username", username));
			criteria.add(Restrictions.eq("password", password));
			List<User> list = criteria.list();
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
	 * @return unregistered user
	 */
	public static User getDeletedUser(String team){
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("username", DELETED_USER));
			
			List<User> list = criteria.list();
			if(list.size() == 0){
				storeUserInfo(team, DELETED_USER, DELETED_USER, "0", DELETED_USER, Application.STANDARD_IMAGE_NOT_FOUND);
				return getDeletedUser(team);
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
	public static User getUser(String team, String username){
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("username", username));
			
			List<User> list = criteria.list();
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
	public static JSONObject getUserInfo(String team, String username){
	
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("username", username));
			
			List<User> list = criteria.list();
			if(list.size() <= 1){
				if(list.size() == 0){
					//No user found
					result.put(Application.RESULT, Application.ERROR);
					result.put(Application.ERROR_MESSAGE, "No user with username - " + username + " found");
				} else {
					Iterator<User> userList = list.iterator();
					User user = userList.next();
					JSONObject userJson = new JSONObject();
					userJson.put("name", user.getName());
					userJson.put("username", user.getUsername());
					userJson.put("contactNumber", user.getContactNumber());
					userJson.put("recordCreationTime", user.getRecordCreationTime());
					userJson.put("image", user.getImage());
					JSONArray userArray = new JSONArray();
					userArray.put(userJson);
					result.put(Application.RESULT, userArray);
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
	 * Delete user
	 * @param username
	 * @return
	 */
	public static JSONObject deleteUser(String team, String username) {
		
		JSONObject result = new JSONObject();
		Session session = null;
		try {
			session = HibernateUtil.getSessionAnnotationFactoryFor(team).openSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("username", username));
			
			List<User> list = criteria.list();
			if(list.size() <= 1){
				if(list.size() == 0){
					//No user found
					result.put(Application.RESULT, Application.ERROR);
					result.put(Application.ERROR_MESSAGE, "No user with username " + username + " found");
				} else {
					User userToDelete = list.get(0);
					session.delete(userToDelete);
					session.getTransaction().commit();
					result.put(Application.RESULT, Application.SUCCESS);
				}
			} else {
				//LOG ERROR HERE ONCE LOGGER IS INTEGRATED
				System.out.println("ERROR. MORE THAN 1 USERS WITH username :"+username+" FOUND");
				result = SystemUtils.generateErrorMessage("More than 1 user with username "+username + " found");
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
