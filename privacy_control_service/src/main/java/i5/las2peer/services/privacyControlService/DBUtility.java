package i5.las2peer.services.privacyControlService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Course;
import model.Manager;
import model.Service;
import model.Student;

public class DBUtility {
	public final static String DB_URL = "localhost";
	public final static int DB_PORT = 1433;
	public final static String DB_NAME = "PrivacyService";
	private final static String DB_USERNAME = "SA";
	private final static String DB_PASSWORD = "privacyIS#1important!";
	
	private Connection dbcon = null;
	
	private PreparedStatement selectManager;
	private PreparedStatement insertManager;
	private PreparedStatement selectService;
	private PreparedStatement insertService;
	private PreparedStatement selectManagerServices;
	private PreparedStatement selectServiceCourses;
	private PreparedStatement selectCourse;
	private PreparedStatement insertCourse;
	private PreparedStatement updateCourse;
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
		
		String text = "SELECT * FROM Manager WHERE Email=?";
		try {
			selectManager = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "INSERT INTO Manager (email, name) VALUES (?,?)";
		try {
			insertManager = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "SELECT * FROM Service WHERE id=?;";		
		try {
			selectService = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "INSERT INTO Service (id, name, managerID) VALUES (?, ?, ?);";
		try {
			insertService = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "SELECT * FROM Service WHERE managerID=?";
		try {
			selectManagerServices = dbcon.prepareStatement(text);
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
		
		text = "SELECT * FROM Course WHERE serviceID=? AND id=?;";
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
		
		text = "UPDATE Course SET name=?, description=? WHERE serviceID=? AND id=?;";
		try {
			updateCourse = dbcon.prepareStatement(text);
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
		
		text = "INSERT INTO Student (email, name) VALUES (?,?);";
		try {
			insertStudent = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	///									Selects									  ///
	/////////////////////////////////////////////////////////////////////////////////
	
	// MANAGER
	
	public JSONObject SelectManager(String managerID) {
		try {
			selectManager.setString(1, managerID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectManager parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectManager.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectManager query.");
			e.printStackTrace();
			return null;
		}
		
		JSONObject retVal = new JSONObject();
		try {
			if (result.next()) {
				retVal.put("email", result.getString("email"));
				retVal.put("name", result.getString("name"));
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectCourse results.");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	// SERVICE
	
	public JSONObject SelectService(String serviceID) {
		try {
			selectService.setString(1, serviceID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectService parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectService.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectService query.");
			e.printStackTrace();
			return null;
		}
		
		// Get basic information
		JSONObject retVal = new JSONObject();
		try {
			if (result.next()) {
				retVal.put("id", result.getString("id"));
				retVal.put("name", result.getString("name"));
				retVal.put("managerID", result.getString("managerID"));
			}
			else {
				return retVal;
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectService results.");
			e.printStackTrace();
		}
		
		//Get courses
		boolean error = false;
		try {
			selectServiceCourses.setString(1, serviceID);
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
					courseJSON.put("serviceID", result.getString("serviceID"));
					courseJSON.put("id", result.getString("id"));
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
	
	// COURSE
	
	public JSONObject SelectCourse(String serviceID, String courseID) {
		try {
			selectCourse.setString(1, serviceID);
			selectCourse.setString(2, courseID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectCourse parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectCourse.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectCourse query.");
			e.printStackTrace();
			return null;
		}
		
		JSONObject retVal = new JSONObject();
		try {
			if (result.next()) {
				retVal.put("id", result.getString("id"));
				retVal.put("serviceID", result.getString("serviceID"));
				retVal.put("name", result.getString("name"));
				retVal.put("description", result.getString("description"));
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectCourse results.");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	public JSONArray SelectManagerServices(String managerID) {
		try {
			selectManagerServices.setString(1, managerID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting InsertService parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectManagerServices.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectManagerCourses query.");
			e.printStackTrace();
			return null;
		}
		
		JSONArray retVal = new JSONArray();
		try {
			while (result.next()) {
				String serviceID = result.getString("id");
				JSONObject serviceJSON = SelectService(serviceID);
				retVal.put(serviceJSON);
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectManagerCourses results.");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////
	///									Inserts									  ///
	/////////////////////////////////////////////////////////////////////////////////
	
	public int InsertManager(Manager manager) {
		try {
			insertManager.setString(1, manager.getEmail());
			insertManager.setString(2, manager.getName());
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting InsertManager parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = insertManager.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing InsertManager statement.");
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	public int InsertService(Service service) {
		try {
			insertService.setString(1, service.getId());
			insertService.setString(2, service.getName());
			insertService.setString(3, service.getManagerID());
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
			insertCourse.setString(1, course.getServiceID());
			insertCourse.setString(2, course.getId());
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
	
	
	public int InsertStudent(Student student) {
		try {
			insertStudent.setString(1, student.getEmail());
			insertStudent.setString(2, student.getName());
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting InsertStudent parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = insertStudent.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing InsertStudent statement.");
			e.printStackTrace();
		}
		return result;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	///									Updates									  ///
	/////////////////////////////////////////////////////////////////////////////////
	
	public int UpdateCourse(Course course) {
		try {
			updateCourse.setString(1, course.getName());
			updateCourse.setString(2, course.getDescription());
			updateCourse.setString(3, course.getServiceID());
			updateCourse.setString(4, course.getId());
		}catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting UpdateCourse parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = updateCourse.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing UpdateCourse statement.");
			e.printStackTrace();
		}
		
		return result;
	}
	
}
