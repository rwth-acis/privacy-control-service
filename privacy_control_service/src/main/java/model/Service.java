package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class Service {
	private String id;
	private String name;
	
	// courseMap<course ID, course>
	private Map<String, Course> courseMap;
	
	public Service() {
		courseMap = new HashMap<String, Course>();
	}
	
	public Service(String id, String name) {
		courseMap = new HashMap<String, Course>();
		this.id = id;
		this.name = name;
	}

	public Map<String, Course> getCourseMap() {
		return courseMap;
	}

	public void setCourseMap(Map<String, Course> courseMap) {
		this.courseMap = courseMap;
	}
	
	public Collection<Course> getAllCourses() {
		return courseMap.values();
	}
	
	public Course getCourse(String courseID) {
		return courseMap.get(courseID);
	}
	
	public Course putCourse(Course course) {
		return courseMap.put(course.getId(), course);
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
	
	public JSONObject toJSON() {
		JSONObject retVal = new JSONObject();
		retVal.put("id", id);
		retVal.put("name", name);
		return retVal;
	}
}
