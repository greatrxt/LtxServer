package gabriel.utilities;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import gabriel.hibernate.entity.Driver;
import gabriel.hibernate.entity.Location;
import gabriel.hibernate.entity.Vehicle;


public class HibernateUtil {

	//XML based configuration
	//private static SessionFactory sessionFactory;
	
	//Annotation based configuration
	private static SessionFactory sessionAnnotationFactory;
    private static SessionFactory buildSessionAnnotationFactory() {
    	try {
            // Create the SessionFactory from hibernate.cfg.xml
        	Configuration configuration = new Configuration();
        	configuration.configure("hibernate.cfg.xml");
        	System.out.println("Hibernate Annotation Configuration loaded");
        	
        	ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        	System.out.println("Hibernate Annotation serviceRegistry created");
        	configuration.addAnnotatedClass(Location.class);
        	configuration.addAnnotatedClass(Driver.class);
        	configuration.addAnnotatedClass(Vehicle.class);
        	SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        	
            return sessionFactory;
/*    		ClassLoader classLoader = HibernateUtil.class.getClassLoader();
    		File propertiesPath = new File(classLoader.getResource("hibernate.cfg.xml").getFile());
    		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().
    		    configure().loadProperties(propertiesPath).build();
    		SessionFactory sf = new Configuration().buildSessionFactory(serviceRegistry);
    		return sf;*/
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
	}

    public static void saveEntity(Object entity){
  		Session session = getSessionAnnotationFactory().openSession();
  		session.beginTransaction();
  		
  		try {
  			session.save(entity);
  			session.flush();
  			session.getTransaction().commit();
  		} catch(Exception e) {
  			e.printStackTrace();
  		} finally{
  			session.close();
  		}
  	}
   
  
	
	public static SessionFactory getSessionAnnotationFactory() {
		if(sessionAnnotationFactory == null) sessionAnnotationFactory = buildSessionAnnotationFactory();
        return sessionAnnotationFactory;
    }
	
	public static void CloseSessionFactory() {
		getSessionAnnotationFactory().close();
    }
	
	
	
}
