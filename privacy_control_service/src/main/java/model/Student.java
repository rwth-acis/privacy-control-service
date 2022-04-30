package model;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import com.google.common.hash.Hashing;


public class Student {
	private String email; //PK
	private String name;
	
	// pseudonyms<Course, pseudonym>
	private Map<Course, String> pseudonyms;
	
	public Student() {
		pseudonyms = new HashMap<Course, String>();
	}
	
	public Student(String email, String name) {
		pseudonyms = new HashMap<Course, String>();
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

	public Map<Course, String> getPseudonyms() {
		return pseudonyms;
	}

	public void setPseudonyms(Map<Course, String> pseudonyms) {
		this.pseudonyms = pseudonyms;
	}
	
	public String getPseudonym(Course course) {
		return pseudonyms.get(course);
	}
	
	
	public static String hash(String input) {
		String hashed = Hashing.sha256()
				.hashString(input, StandardCharsets.UTF_8)
				.toString();
		return hashed;
	}
	
	public static String hash(String input, String salt) {
		String hashed = Hashing.sha256()
				.hashString(input + salt, StandardCharsets.UTF_8)
				.toString();
		return hashed;
	}
	
	public String createPseudonym(Course course) {
		Random random = new Random();
		String hash = Student.hash(email, String.valueOf(random.nextGaussian()));
		pseudonyms.put(course, hash);
		return hash;
	}
	
	
	public JSONObject toJSON() {
		JSONObject retVal = new JSONObject();
		retVal.put("name", name);
		retVal.put("email", email);
		return retVal;
	}

}
