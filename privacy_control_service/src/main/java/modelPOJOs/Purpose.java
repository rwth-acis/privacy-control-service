package modelPOJOs;

import org.json.JSONObject;

public class Purpose {
	private int id;
	private String title;
	private String description;
	private int version;
	
	public Purpose() {}
	
	public Purpose(int id, String title, String description, int version) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.version = version;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "Purpose [id=" + id + ", title=" + title + ", description=" + description + ", version=" + version + "]";
	}
	
	public JSONObject toJSON() {
		JSONObject retVal = new JSONObject();
		retVal.put("id", id);
		retVal.put("title", title);
		retVal.put("description", description);
		retVal.put("version", version);
		return retVal;
	}
}
