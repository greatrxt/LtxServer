package gabriel.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {
	
	private static void createDatabaseIfNotExists(){
	
		Statement statement;
		try {
			Class.forName("org.postgresql.Driver");
			Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "admin");
			statement = c.createStatement();
			statement.executeUpdate("CREATE DATABASE LocTrack");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * 
	 * @param schemaName
	 * @return
	 */
	public static boolean schemaExists(String schemaName){
		
		Connection connection = null;
		
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/LocTrack", "postgres", "admin");
			
			boolean schemaExists = false;
			String createSchema = "SELECT schema_name FROM information_schema.schemata WHERE schema_name = '"+schemaName+"';";
			
			Statement createSchemaStatement = connection.createStatement();
			ResultSet resultSet = createSchemaStatement.executeQuery(createSchema);
			schemaExists = resultSet.next();
			return schemaExists;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(connection!=null){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Failed to verify if schema exists");
		return true;	//do nothing if error in above block
	}
	
	/**
	 * 
	 * @param schemaName
	 */
	public static void createSchemaIfNotExists(String schemaName){
	
		Connection connection = null;
		
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/LocTrack", "postgres", "admin");
		
			if(!schemaExists(schemaName)){
				Statement statement = connection.createStatement();
				statement.executeUpdate("CREATE SCHEMA "+schemaName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(connection!=null){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
	}
}
