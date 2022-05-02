package model;

import org.json.JSONObject;


public class Student {
	private String email; //PK
	private String name;
	
	public Student() {}
	
	public Student(String email, String name) {
		this.email = email;
		this.name = name;		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}	
	
	public JSONObject toJSON() {
		JSONObject retVal = new JSONObject();
		retVal.put("name", name);
		retVal.put("email", email);
		return retVal;
	}
	
	@Override
	public String toString() {
		return "Student [email=" + email + ", name=" + name + "]";
	}
}
