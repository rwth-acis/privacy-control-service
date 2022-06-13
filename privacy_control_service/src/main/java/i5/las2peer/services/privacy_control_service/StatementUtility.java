package i5.las2peer.services.privacy_control_service;

import static org.junit.Assert.fail;

import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class StatementUtility {
	public static final String DISPLAY_LANGUAGE = "en-US";
	
	private JSONObject statement1;
	private JSONObject statement2;
	private JSONObject statement3;
	
	public JSONObject parseAndFormat(String rawStatement) {
		JSONTokener tokener = new JSONTokener(rawStatement);
		JSONObject tmp = new JSONObject(tokener);
		JSONObject retVal = new JSONObject();
		
		// Whole statement
		JSONObject statement = tmp.getJSONObject("statement");
		retVal.put("statement", statement);
		
		// Best readable verb
		JSONObject verbJSON = statement.getJSONObject("verb");
		boolean verbHasName = false;
		if (verbJSON.has("display")) {
			JSONObject displayJSON = verbJSON.getJSONObject("display");
			if (displayJSON.has("en-US")) {
				String verbName = displayJSON.getString("en-US");
				retVal.put("verb", verbName);
				verbHasName = true;
			}
		}
		if (!verbHasName) {
			String verbID = verbJSON.getString("id");
			retVal.put("verb", verbID);
		}
		
		// Best readable object
		JSONObject objectJSON = statement.getJSONObject("object");
		boolean objectHasName = false;
		if (objectJSON.has("definition")) {
			JSONObject definition = objectJSON.getJSONObject("definition");
			if (definition.has("name")) {
				JSONObject nameJSON = definition.getJSONObject("name");
				if (nameJSON.has("en-US")) {
					String objectName = nameJSON.getString("en-US");
					retVal.put("object", objectName);
					objectHasName = true;
				}
			}
		}
		if (!objectHasName) {
			retVal.put("object", objectJSON.get("id"));
		}
		
		// Time and date
		if (statement.has("timestamp")) {
			retVal.put("timestamp", statement.get("timestamp"));
		}
		else {
			retVal.put("timestamp", statement.get("stored"));
		}
		
		return retVal;
	}
	
	public JSONObject extractCoreStatement(JSONObject originalStatement) {
		JSONObject coreStatement = new JSONObject();
		
		try {		
			JSONObject origActor = originalStatement.getJSONObject("actor");
			JSONObject coreActor = extractCoreActor(origActor);
			coreStatement.put("actor", coreActor);
			
			JSONObject origVerb = originalStatement.getJSONObject("verb");
			JSONObject coreVerb = extractCoreVerb(origVerb);
			coreStatement.put("verb", coreVerb);
						
			
			JSONObject origObject = originalStatement.getJSONObject("object");
			JSONObject coreObject = extractCoreObject(origObject);
			coreStatement.put("object", coreObject);
			
			String timestamp = originalStatement.getString("timestamp");
			coreStatement.put("timestamp", timestamp);
		} catch (JSONException e) {
			e.printStackTrace();
			// TODO: add logger
			return null;
		}
		
		return coreStatement;
	}
	
	public JSONObject extractCoreActor(JSONObject originalActor) {
		JSONObject coreActor = new JSONObject();
		try {
			JSONObject origAccount = originalActor.getJSONObject("account");
			String actorName = origAccount.getString("name");
			String actorHomePage = origAccount.getString("homePage");
			
			JSONObject coreAccount = new JSONObject();
			coreAccount.put("name", actorName);
			coreAccount.put("homePage", actorHomePage);
			coreActor.put("account", coreAccount);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return coreActor;
	}
	
	public JSONObject extractCoreVerb(JSONObject originalVerb) {
		JSONObject coreVerb = new JSONObject();
		try {
			String verbID = originalVerb.getString("id");
			coreVerb.put("id", verbID);
			
			JSONObject origDisplay = originalVerb.getJSONObject("display");
			String verbName = origDisplay.getString(DISPLAY_LANGUAGE);
			JSONObject coreDisplay = new JSONObject();
			coreDisplay.put(DISPLAY_LANGUAGE, verbName);
			coreVerb.put("display", coreDisplay);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return coreVerb;
	}
	
	public JSONObject extractCoreObject(JSONObject originalObject) {
		JSONObject coreObject = new JSONObject();
		try {
			String objectID = originalObject.getString("id");
			coreObject.put("id", objectID);
			
			JSONObject origDefinition = originalObject.getJSONObject("definition");
			JSONObject origName = origDefinition.getJSONObject("name");
			String objectName = origName.getString(DISPLAY_LANGUAGE);
			JSONObject coreName = new JSONObject();
			coreName.put(DISPLAY_LANGUAGE, objectName);
			JSONObject coreDefinition = new JSONObject();
			coreDefinition.put("name", coreName);
			coreObject.put("definition", coreDefinition);			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return coreObject;
	}
	
	public String getActorEmail(JSONObject statement) {
		String retVal;
		try {
			JSONObject actorJSON = statement.getJSONObject("actor");
			JSONObject accountJSON = actorJSON.getJSONObject("account");
			retVal = accountJSON.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return retVal;
	}
	
	public String getVerbDisplayName(JSONObject statement) {
		String retVal;
		try {
			JSONObject verbJSON = statement.getJSONObject("verb");
			JSONObject displayJSON = verbJSON.getJSONObject("display");
			retVal = displayJSON.getString(DISPLAY_LANGUAGE);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return retVal;
	}
	
	public String getObjectName(JSONObject statement) {
		String retVal;
		try {
			JSONObject objectJSON = statement.getJSONObject("object");
			JSONObject definitionJSON = objectJSON.getJSONObject("definition");
			JSONObject nameJSON = definitionJSON.getJSONObject("name");
			retVal = nameJSON.getString(DISPLAY_LANGUAGE);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return retVal;
	}
}
