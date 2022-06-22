package model;
import org.json.JSONObject;

public class Service {
	private String id;		  //PK
	private String name;
	private String managerID; //FK
	
	public Service() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getManagerID() {
		return managerID;
	}

	public void setManagerID(String managerID) {
		this.managerID = managerID;
	}

	public JSONObject toJSON() {
		JSONObject retVal = new JSONObject();
		retVal.put("id", id);
		retVal.put("name", name);
		return retVal;
	}
	
	public String toDBInsertString() {
		String retVal = "INSERT INTO Service (id, name) VALUES (" + id + ", '" + name + "');";
		return retVal;
	}
	
	public static String getDBSelectString(int id) {
		String retVal =  "SELECT * FROM Service WHERE id=" + id + ";";
		return retVal;
	}
	
	public static String getDBCourses(int id) {
		String retVal = "SELECT * FROM Course WHERE serviceID=" + id + ";";
		return retVal;
	}
}
