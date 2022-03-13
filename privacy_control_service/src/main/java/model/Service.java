package model;

import java.util.HashMap;
import java.util.Map;

public class Service {
	private String id;
	private String name;
	
	private Map<String, Course> courseMap;
	
	public Service() {
		courseMap = new HashMap<String, Course>();
	}

	public Map<String, Course> getCourseMap() {
		return courseMap;
	}

	public void setCourseMap(Map<String, Course> courseMap) {
		this.courseMap = courseMap;
	}
	
	public Course getCourse(String courseID) {
		return courseMap.get(courseID);
	}
	
	public Course putCourse(Course course) {
		return courseMap.put(null, course);
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
}
