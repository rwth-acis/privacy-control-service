package model;

import java.sql.Date;

public class Consent {
	private String studentID;
	private String courseID;
	private String serviceID;
	private int purposeID;
	private Date datetime;
	
	public Consent() {}

	public Consent(String studentID, String courseID, String serviceID, int purposeID, Date datetime) {
		super();
		this.studentID = studentID;
		this.courseID = courseID;
		this.serviceID = serviceID;
		this.purposeID = purposeID;
		this.datetime = datetime;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public int getPurposeID() {
		return purposeID;
	}

	public void setPurposeID(int purposeID) {
		this.purposeID = purposeID;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
}
