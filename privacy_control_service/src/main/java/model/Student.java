package model;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.common.hash.Hashing;


public class Student {
	private String id;
	private String name;
	private String email;
	private Map<Course, String> pseudonyms;
	
	public Student() {
		pseudonyms = new HashMap<Course, String>();
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
		String hash = Student.hash(id, String.valueOf(random.nextGaussian()));
		pseudonyms.put(course, hash);
		return hash;
	}

}
