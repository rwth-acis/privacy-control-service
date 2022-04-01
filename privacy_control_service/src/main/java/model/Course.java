package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import java.util.Map;

public class Course {
	private String id;
	private String name;
	private Map<Student, Set<Purpose>> studentToPurposesMap;
	
	public Course() {
		studentToPurposesMap = new HashMap<Student, Set<Purpose>>();
	}
	
	public Course(String id, String name) {
		studentToPurposesMap = new HashMap<Student, Set<Purpose>>();
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

	public Map<Student, Set<Purpose>> getStudentToPurposesMap() {
		return studentToPurposesMap;
	}

	public void setStudentToPurposesMap(Map<Student, Set<Purpose>> studentToPurposesMap) {
		this.studentToPurposesMap = studentToPurposesMap;
	}
	
	public Set<Purpose> getStudentPurposes(Student student) {
		return studentToPurposesMap.get(student);
	}
	
	public void addStudentPurpose(Student student, Purpose purpose) {
		Set<Purpose> studentPurposes = studentToPurposesMap.get(student);
		if (studentPurposes == null) {
			studentPurposes = new HashSet<Purpose>();
			studentToPurposesMap.put(student, studentPurposes);
		}
		studentPurposes.add(purpose);
	}
	
	public void addStudent(Student student) {
		Set<Purpose> tmp = new HashSet<Purpose>();
		studentToPurposesMap.put(student, tmp);
	}
	
	public boolean hasStudent(Student student) {
		return studentToPurposesMap.containsKey(student);
	}

	
	public JSONObject toJSON() {
		JSONObject retVal = new JSONObject();
		retVal.put("id", id);
		retVal.put("name", name);
		return retVal;
	}
	
}
