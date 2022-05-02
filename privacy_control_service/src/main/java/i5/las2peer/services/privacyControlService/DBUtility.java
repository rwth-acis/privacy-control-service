package i5.las2peer.services.privacyControlService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Course;
import model.Manager;
import model.Purpose;
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
	private PreparedStatement selectPurpose;
	private PreparedStatement insertPurpose;
	private PreparedStatement updatePurpose;
	private PreparedStatement selectPurposesInCourse;
	private PreparedStatement insertPurposeInCourse;
	private PreparedStatement selectStudentsInCourse;
	private PreparedStatement insertStudentInCourse;
	private PreparedStatement selectCoursesWithStudent;
	

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
	// TODO: Put all into one trycatch
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
		
		text = "SELECT * FROM Student WHERE email=?;";
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
		
		text = "SELECT * FROM Purpose WHERE id=?;";
		try {
			selectPurpose = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "INSERT INTO Purpose (id, title, description, version) "
				+ "VALUES (?,?,?,?);";
		try {
			insertPurpose = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "UPDATE Purpose SET title=?, description=?, version=? WHERE id=?";
		try {
			updatePurpose = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "SELECT * FROM PurposeInCourse WHERE serviceID=? AND courseID=?;";
		try {
			selectPurposesInCourse = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "INSERT INTO PurposeInCourse (serviceID, courseID, purposeID)"
				+ " VALUES (?,?,?);";
		try {
			insertPurposeInCourse = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "SELECT * FROM StudentInCourse WHERE serviceID=? AND courseID=?;";
		try {
			selectStudentsInCourse = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "INSERT INTO StudentInCourse (serviceID, courseID, studentID, pseudonym)"
				+ " VALUES (?,?,?,?);";
		try {
			insertStudentInCourse = dbcon.prepareStatement(text);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Could not prepare statement: " + text);
			e.printStackTrace();
			return false;
		}
		
		text = "SELECT serviceID, courseID FROM StudentInCourse WHERE studentID=? ORDER BY serviceID ASC;";
		try {
			selectCoursesWithStudent = dbcon.prepareStatement(text);
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
			PrivacyControlService.logger.severe("Error while retrieving SelectManager results.");
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
		
		return retVal;
	}
	
	
	public JSONObject SelectServiceWithCourses(String serviceID) {
		JSONObject retVal = SelectService(serviceID);
		
		boolean error = false;
		try {
			selectServiceCourses.setString(1, serviceID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectServiceCourses parameters.");
			e.printStackTrace();
			error = true;			
		}
		
		ResultSet result = null;
		if (!error) {
			try {
				result = selectServiceCourses.executeQuery();
			} catch (SQLException e1) {
				PrivacyControlService.logger.severe("Error while executing SelectServiceCourses query.");
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
				PrivacyControlService.logger.severe("Error while retrieving SelectServiceCourses results.");
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
			PrivacyControlService.logger.severe("Error while setting SelectManagerServices parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectManagerServices.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectManagerServices query.");
			e.printStackTrace();
			return null;
		}
		
		JSONArray retVal = new JSONArray();
		try {
			while (result.next()) {
				String serviceID = result.getString("id");
				JSONObject serviceJSON = SelectServiceWithCourses(serviceID);
				retVal.put(serviceJSON);
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectManagerServices results.");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	// PURPOSE
	
	public JSONObject SelectPurpose(int purposeID) {
		try {
			selectPurpose.setInt(1, purposeID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectPurpose parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectPurpose.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectPurpose query.");
			e.printStackTrace();
			return null;
		}
		
		JSONObject retVal = new JSONObject();
		try {
			if (result.next()) {
				retVal.put("id", result.getInt("id"));
				retVal.put("title", result.getString("title"));
				retVal.put("description", result.getString("description"));
				retVal.put("version", result.getInt("version"));
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectPurpose results.");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	// PURPOSE IN COURSE
	
	public List<Integer> SelectPurposesInCourse(String serviceID, String courseID) {
		try {
			selectPurposesInCourse.setString(1, serviceID);
			selectPurposesInCourse.setString(2, courseID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectPurposesInCourse parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectPurposesInCourse.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectPurposesInCourse query.");
			e.printStackTrace();
			return null;
		}
		
		List<Integer> retVal = new ArrayList<>();
		try {
			while (result.next()) {
				int purposeID = result.getInt("purposeID");
				retVal.add(purposeID);
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectPurposesInCourse results.");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	// STUDENT
	
	public JSONObject SelectStudent(String studentID) {
		try {
			selectStudent.setString(1, studentID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectStudent parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectStudent.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectStudent query.");
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
			PrivacyControlService.logger.severe("Error while retrieving SelectStudent results.");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	// STUDENT IN COURSE
	
	public JSONArray SelectStudentsInCourse(String serviceID, String courseID) {
		try {
			selectStudentsInCourse.setString(1, serviceID);
			selectStudentsInCourse.setString(2, courseID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectStudentsInCourse parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectStudentsInCourse.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectStudentsInCourse query.");
			e.printStackTrace();
			return null;
		}
		
		JSONArray retVal = new JSONArray();
		try {
			while (result.next()) {
				JSONObject sic = new JSONObject();
				sic.put("serviceID", result.getString("serviceID"));
				sic.put("courseID", result.getString("courseID"));
				sic.put("email", result.getString("studentID"));
				sic.put("pseudonym", result.getString("pseudonym"));
				
				retVal.put(sic);
			}
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectStudentsInCourse results.");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	// COURSE WITH STUDENT
	
	public JSONArray SelectCoursesWithStudent(String studentID) {
		try {
			selectCoursesWithStudent.setString(1, studentID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting SelectCoursesWithStudent parameters.");
			e.printStackTrace();
			return null;
		}
		
		ResultSet result;
		try {
			result = selectCoursesWithStudent.executeQuery();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing SelectCoursesWithStudent query.");
			e.printStackTrace();
			return null;
		}
		
		JSONArray retVal = new JSONArray();
		try {
			String lastServiceID = "";
			JSONObject serviceJSON = null;
			JSONArray serviceCourses = null;
			while (result.next()) {
				String serviceID = result.getString("serviceID");
				String courseID = result.getString("courseID");
				
				if (!serviceID.equals(lastServiceID)) {
					// if new service
					if (serviceJSON != null) {
						// if not first time
						serviceJSON.put("courses", serviceCourses);
						retVal.put(serviceJSON);
					}
					serviceJSON = SelectService(serviceID);
					serviceCourses = new JSONArray();
					lastServiceID = serviceID;
				}
				
				JSONObject courseJSON = SelectCourse(serviceID, courseID);
				serviceCourses.put(courseJSON);
			}
			// for last result
			serviceJSON.put("courses", serviceCourses);
			retVal.put(serviceJSON);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while retrieving SelectCoursesWithStudent results.");
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
	
	public int InsertPurpose(Purpose purpose) {
		try {
			insertPurpose.setInt(1, purpose.getId());
			insertPurpose.setString(2, purpose.getTitle());
			insertPurpose.setString(3, purpose.getDescription());
			insertPurpose.setInt(4, purpose.getVersion());
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting InsertPurpose parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = insertPurpose.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing InsertPurpose statement.");
			e.printStackTrace();
		}
		return result;
	}
	
	public int InsertPurposeInCourse(int purposeID, String serviceID, String courseID) {
		try {
			insertPurposeInCourse.setString(1, serviceID);
			insertPurposeInCourse.setString(2, courseID);
			insertPurposeInCourse.setInt(3, purposeID);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting InsertPurposeInCourse parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = insertPurposeInCourse.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing InsertPurposeInCourse statement.");
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
	
	public int InsertStudentInCourse(String studentID, String serviceID, String courseID, String pseudonym) {
		try {
			insertStudentInCourse.setString(1, serviceID);
			insertStudentInCourse.setString(2, courseID);
			insertStudentInCourse.setString(3, studentID);
			insertStudentInCourse.setString(4, pseudonym);
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting InsertStudentInCourse parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = insertStudentInCourse.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing InsertStudentInCourse statement.");
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
	
	public int UpdatePurpose(Purpose purpose) {
		try {
			updatePurpose.setInt(1, purpose.getId());
			updatePurpose.setString(2, purpose.getTitle());
			updatePurpose.setString(3, purpose.getDescription());
			updatePurpose.setInt(4, purpose.getVersion());
		}catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while setting UpdatePurpose parameters.");
			e.printStackTrace();
			return -1;
		}
		
		int result = 0;
		try {
			result = updateCourse.executeUpdate();
		} catch (SQLException e) {
			PrivacyControlService.logger.severe("Error while executing UpdatePurpose statement.");
			e.printStackTrace();
		}
		
		return result;
	}
	
}
