package i5.las2peer.services.privacyControlService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Course;
import model.Service;

public class DBUtility {
	public final static String DB_URL = "localhost";
	public final static int DB_PORT = 1433;
	public final static String DB_NAME = "PrivacyService";
	private final static String DB_USERNAME = "SA";
	private final static String DB_PASSWORD = "privacyIS#1important!";
	
	private Connection dbcon = null;
	
	private PreparedStatement selectService;
	private PreparedStatement insertService;
	private PreparedStatement selectServiceCourses;
	private PreparedStatement selectCourse;
	private PreparedStatement insertCourse;
	private PreparedStatement selectStudent;
	private PreparedStatement insertStudent;

	public static Connection getConnection() {
		String connectionUrl =
                "jdbc:sqlserver://" + DB_URL + ":" + DB_PORT + ";"
                + "database=" + DB_NAME + ";"
                + "user=" + DB_USERNAME + ";"
                + "password=" + DB_PASSWORD + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=30;";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(connectionUrl);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not connect to PrivacyControlService database.");
		}
        
        return connection;
	}
	
	public boolean establishConnection(
			String dbURL,
			int dbPort,
			String dbName,
			String dbUsername,
			String dbPassword
			) {
		String connectionUrl =
                "jdbc:sqlserver://" + dbURL + ":" + dbPort + ";"
                + "database=" + dbName + ";"
                + "user=" + dbUsername + ";"
                + "password=" + dbPassword + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=30;";
		try {
			dbcon = DriverManager.getConnection(connectionUrl);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not connect to PrivacyControlService database.");
			return false;
		}
		
		return true;
	}
	
	// TODO: Remove all printStackTrace
	public boolean prepareStatements() {
		if (dbcon == null) {
			PrivacyControlService.logger.severe("Must first establish database connection.");
			return false;
		}
		
		String text = "SELECT * FROM Service WHERE id=?;";		
		try {
			selectService = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "INSERT INTO Service (id, name) VALUES (?, ?);";
		try {
			insertService = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "SELECT * FROM Course WHERE serviceID=?;";
		try {
			selectServiceCourses = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "SELECT * FROM Course WHERE courseID=? AND id=?;";
		try {
			selectCourse = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "INSERT INTO Course (serviceID, id, name, description) "
				+ "VALUES(?,?,?,?);";
		try {
			insertCourse = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "SELECT * FROM Student WHERE id=?;";
		try {
			selectStudent = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "INSERT INTO Student (id, name, email) VALUES (?,?,?);";
		try {
			insertStudent = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public JSONObject SelectService(int serviceID) {
		try {
			selectService.setInt(1, serviceID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectService parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectService.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectCourse query.");
			e.printStackTrace();
			return null;
		}
		
		// Get basic information
		JSONObject retVal = new JSONObject();
		try {
			if (result.next()) {
				retVal.put("id", result.getInt("id"));
				retVal.put("name", result.getString("name"));
			}
			else {
				return retVal;
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectCourse results.");
			e.printStackTrace();
		}
		
		//Get courses
		boolean error = false;
		try {
			selectServiceCourses.setInt(1, serviceID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectServiceCourses parameters.");
			e.printStackTrace();
			error = true;			
		}
		
		if (!error) {
			try {
				result = selectServiceCourses.executeQuery();
			} catch (SQLException e1) {
				PrivacyControlService.logger.severe("Error while executing SelectCourse query.");
				e1.printStackTrace();
				error = true;
			};
		}
		
		if (!error) {
			JSONArray coursesJSON = new JSONArray();
			try {
				while (result.next()) {
					JSONObject courseJSON = new JSONObject();
					courseJSON.put("serviceID", result.getInt("serviceID"));
					courseJSON.put("id", result.getInt("id"));
					courseJSON.put("name", result.getString("name"));
					courseJSON.put("description", result.getString("description"));
					coursesJSON.put(courseJSON);
				}
			} catch (SQLException e) {
				PrivacyControlService.logger.severe("Error while retrieving SelectCourse results.");
				e.printStackTrace();
			}
			retVal.put("courses", coursesJSON);
		}
		
		return retVal;
	}
	
	public int InsertService(Service service) {
		try {
			insertService.setInt(1, service.getId());
			insertService.setString(2, service.getName());
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting InsertService parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = insertService.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing InsertService statement.");
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int InsertCourse(Course course) {
		try {
			insertCourse.setInt(1, course.getServiceID());
			insertCourse.setInt(2, course.getId());
			insertCourse.setString(3, course.getName());
			insertCourse.setString(4, course.getDescription());
		}catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting InsertCourse parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = insertCourse.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing InsertCourse statement.");
			e.printStackTrace();
		}
		
		return result;
	}
	
	
}
