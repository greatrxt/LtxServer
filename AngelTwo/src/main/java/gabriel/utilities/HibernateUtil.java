package gabriel.utilities;

import java.util.HashMap;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import gabriel.hibernate.entity.Driver;
import gabriel.hibernate.entity.Location;
import gabriel.hibernate.entity.Vehicle;


public class HibernateUtil {


	
	//Annotation based configuration
	//private static SessionFactory sessionAnnotationFactory;
	private static HashMap<String, SessionFactory> sessionAnnotationFactoryMap = new HashMap<>();
	
    private static SessionFactory buildSessionAnnotationFactory(String schemaName) {
    	try {
    		
    		DbUtils.createSchemaIfNotExists(schemaName);
            // Create the SessionFactory from hibernate.cfg.xml
        	Configuration configuration = new Configuration();
        	configuration.configure("hibernate.cfg.xml");
        	//configuration.configure();
        	/*configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        	configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/"+"LocTrack");
        	configuration.setProperty("hibernate.connection.username", "postgres");
        	configuration.setProperty("hibernate.connection.password", "admin");
        	configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        	configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        	
        	configuration.setProperty("hibernate.show_sql", "true");
        	configuration.setProperty("format_sql", "true");
        	configuration.setProperty("use_sql_comments", "true");
        	configuration.setProperty("hibernate.c3p0.min_size", "5");
        	configuration.setProperty("hibernate.c3p0.max_size", "20");
        	configuration.setProperty("hibernate.c3p0.timeout", "300");
        	configuration.setProperty("hibernate.c3p0.max_statements", "50");
        	configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");*/
        	
        	configuration.setProperty("hibernate.default_schema", schemaName);
        	
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

	
	public static SessionFactory getSessionAnnotationFactoryFor(String companyName) {
		
		SessionFactory sessionAnnotationFactory = null;
		
		if(sessionAnnotationFactoryMap.containsKey(companyName)){
			sessionAnnotationFactory = sessionAnnotationFactoryMap.get(companyName);
		}
		
		if(sessionAnnotationFactory == null) {
			sessionAnnotationFactory = buildSessionAnnotationFactory(companyName);
			sessionAnnotationFactoryMap.put(companyName, sessionAnnotationFactory);
		}
		
        return sessionAnnotationFactory;
    }
	
	public static void CloseSessionFactory(String schemaName) {
		getSessionAnnotationFactoryFor(schemaName).close();
    }
	
	
	
}
