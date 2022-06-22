package model;

import org.json.JSONObject;


public class Course {
	private String serviceID; //PK
	private String id;		  //PK
	private String name;
	private String description;
	
	public Course() {}
	
	public Course(String serviceID, String id, String name) {
		this.serviceID = serviceID;
		this.id = id;
		this.name = name;
	}

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
	
	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JSONObject toJSON() {
		JSONObject retVal = new JSONObject();
		retVal.put("id", id);
		retVal.put("name", name);
		return retVal;
	}

	@Override
	public String toString() {
		return "Course [serviceID=" + serviceID + ", id=" + id + ", name=" + name + ", description=" + description
				+ "]";
	}
}
