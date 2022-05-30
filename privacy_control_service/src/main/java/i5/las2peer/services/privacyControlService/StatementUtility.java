package i5.las2peer.services.privacyControlService;

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
	
	public JSONObject getStatement1() {
		String raw = "{\r\n"
				+ "  \"stored\": \"2022-05-25T18:35:23.938Z\",\r\n"
				+ "  \"active\": true,\r\n"
				+ "  \"completedForwardingQueue\": [],\r\n"
				+ "  \"failedForwardingLog\": [],\r\n"
				+ "  \"client\": \"5f218268c986fb0029f925da\",\r\n"
				+ "  \"lrs_id\": \"5d75244b2ba8a000256244ab\",\r\n"
				+ "  \"completedQueues\": [\r\n"
				+ "    \"STATEMENT_FORWARDING_QUEUE\",\r\n"
				+ "    \"STATEMENT_QUERYBUILDERCACHE_QUEUE\",\r\n"
				+ "    \"STATEMENT_PERSON_QUEUE\"\r\n"
				+ "  ],\r\n"
				+ "  \"activities\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/view.php?id=36\"\r\n"
				+ "  ],\r\n"
				+ "  \"hash\": \"3d436746bdcde41dce258740a145a8f2b382c1e6\",\r\n"
				+ "  \"agents\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/|jovanovic.boris@rwth-aachen.de\"\r\n"
				+ "  ],\r\n"
				+ "  \"statement\": {\r\n"
				+ "    \"authority\": {\r\n"
				+ "      \"objectType\": \"Agent\",\r\n"
				+ "      \"name\": \"New Client\",\r\n"
				+ "      \"mbox\": \"mailto:hello@learninglocker.net\"\r\n"
				+ "    },\r\n"
				+ "    \"stored\": \"2022-05-25T18:35:23.938Z\",\r\n"
				+ "    \"context\": {\r\n"
				+ "      \"extensions\": {\r\n"
				+ "        \"https://tech4comp.de/xapi/context/extensions/actorRoles\": [\r\n"
				+ "          {\r\n"
				+ "            \"roleid\": 1,\r\n"
				+ "            \"rolename\": \"student\"\r\n"
				+ "          }\r\n"
				+ "        ],\r\n"
				+ "        \"https://tech4comp.de/xapi/context/extensions/courseInfo\": {\r\n"
				+ "          \"courseplatform\": \"Moodle\",\r\n"
				+ "          \"courseid\": 11\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    \"actor\": {\r\n"
				+ "      \"name\": \"Boris Jovanovic\",\r\n"
				+ "      \"account\": {\r\n"
				+ "        \"name\": \"jovanovic.boris@rwth-aachen.de\",\r\n"
				+ "        \"homePage\": \"https://moodle.tech4comp.dbis.rwth-aachen.de/\"\r\n"
				+ "      },\r\n"
				+ "      \"objectType\": \"Agent\"\r\n"
				+ "    },\r\n"
				+ "    \"timestamp\": \"2022-05-25T18:35Z\",\r\n"
				+ "    \"version\": \"1.0.0\",\r\n"
				+ "    \"id\": \"a806136c-19e6-4cbe-8a14-9e7a042eec1e\",\r\n"
				+ "    \"verb\": {\r\n"
				+ "      \"display\": {\r\n"
				+ "        \"en-US\": \"viewed\"\r\n"
				+ "      },\r\n"
				+ "      \"id\": \"http://id.tincanapi.com/verb/viewed\"\r\n"
				+ "    },\r\n"
				+ "    \"object\": {\r\n"
				+ "      \"definition\": {\r\n"
				+ "        \"interactionType\": \"other\",\r\n"
				+ "        \"name\": {\r\n"
				+ "          \"en-US\": \"discussion_813\"\r\n"
				+ "        },\r\n"
				+ "        \"type\": \"https://w3id.org/xapi/seriousgames/activity-types/item\"\r\n"
				+ "      },\r\n"
				+ "      \"id\": \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/view.php?id=36\",\r\n"
				+ "      \"objectType\": \"Activity\"\r\n"
				+ "    }\r\n"
				+ "  },\r\n"
				+ "  \"hasGeneratedId\": true,\r\n"
				+ "  \"deadForwardingQueue\": [],\r\n"
				+ "  \"voided\": false,\r\n"
				+ "  \"verbs\": [\r\n"
				+ "    \"http://id.tincanapi.com/verb/viewed\"\r\n"
				+ "  ],\r\n"
				+ "  \"personaIdentifier\": \"5f6b65010656d7981c1ab2d0\",\r\n"
				+ "  \"processingQueues\": [],\r\n"
				+ "  \"person\": {\r\n"
				+ "    \"_id\": \"5f6b650105cd34003fcb7bcb\",\r\n"
				+ "    \"display\": \"Boris Jovanovic\"\r\n"
				+ "  },\r\n"
				+ "  \"__v\": 1,\r\n"
				+ "  \"timestamp\": \"2022-05-25T18:35:00.000Z\",\r\n"
				+ "  \"relatedActivities\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/view.php?id=36\"\r\n"
				+ "  ],\r\n"
				+ "  \"relatedAgents\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/|jovanovic.boris@rwth-aachen.de\",\r\n"
				+ "    \"mailto:hello@learninglocker.net\"\r\n"
				+ "  ],\r\n"
				+ "  \"organisation\": \"5d75242f2ba8a000256244a7\",\r\n"
				+ "  \"_id\": \"628e76ecaf0d010080d5d9f7\",\r\n"
				+ "  \"registrations\": [],\r\n"
				+ "  \"pendingForwardingQueue\": []\r\n"
				+ "}";
		
		return parseAndFormat(raw);
	}
	
	public JSONObject getStatement2() {
		String raw = "{\r\n"
				+ "  \"stored\": \"2022-05-25T18:38:16.639Z\",\r\n"
				+ "  \"active\": true,\r\n"
				+ "  \"completedForwardingQueue\": [],\r\n"
				+ "  \"failedForwardingLog\": [],\r\n"
				+ "  \"client\": \"5f218268c986fb0029f925da\",\r\n"
				+ "  \"lrs_id\": \"5d75244b2ba8a000256244ab\",\r\n"
				+ "  \"completedQueues\": [\r\n"
				+ "    \"STATEMENT_QUERYBUILDERCACHE_QUEUE\",\r\n"
				+ "    \"STATEMENT_FORWARDING_QUEUE\",\r\n"
				+ "    \"STATEMENT_PERSON_QUEUE\"\r\n"
				+ "  ],\r\n"
				+ "  \"activities\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/discuss.php?d=813#p3201\"\r\n"
				+ "  ],\r\n"
				+ "  \"hash\": \"17b1212db92ea882e038ebe0b908e4d81672af6d\",\r\n"
				+ "  \"agents\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/|jovanovic.boris@rwth-aachen.de\"\r\n"
				+ "  ],\r\n"
				+ "  \"statement\": {\r\n"
				+ "    \"authority\": {\r\n"
				+ "      \"objectType\": \"Agent\",\r\n"
				+ "      \"name\": \"New Client\",\r\n"
				+ "      \"mbox\": \"mailto:hello@learninglocker.net\"\r\n"
				+ "    },\r\n"
				+ "    \"stored\": \"2022-05-25T18:38:16.639Z\",\r\n"
				+ "    \"context\": {\r\n"
				+ "      \"extensions\": {\r\n"
				+ "        \"https://tech4comp.de/xapi/context/extensions/actorRoles\": [\r\n"
				+ "          {\r\n"
				+ "            \"roleid\": 1,\r\n"
				+ "            \"rolename\": \"student\"\r\n"
				+ "          }\r\n"
				+ "        ],\r\n"
				+ "        \"https://tech4comp.de/xapi/context/extensions/courseInfo\": {\r\n"
				+ "          \"courseplatform\": \"Moodle\",\r\n"
				+ "          \"courseid\": 11\r\n"
				+ "        },\r\n"
				+ "        \"https://tech4comp.de/xapi/context/extensions/postParentID\": {\r\n"
				+ "          \"parentid\": 2109\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    \"actor\": {\r\n"
				+ "      \"name\": \"Boris Jovanovic\",\r\n"
				+ "      \"account\": {\r\n"
				+ "        \"name\": \"jovanovic.boris@rwth-aachen.de\",\r\n"
				+ "        \"homePage\": \"https://moodle.tech4comp.dbis.rwth-aachen.de/\"\r\n"
				+ "      },\r\n"
				+ "      \"objectType\": \"Agent\"\r\n"
				+ "    },\r\n"
				+ "    \"timestamp\": \"2022-05-25T18:38:41Z\",\r\n"
				+ "    \"version\": \"1.0.0\",\r\n"
				+ "    \"id\": \"b5889a6e-8960-41cd-bc70-ad0d3a4d09dd\",\r\n"
				+ "    \"verb\": {\r\n"
				+ "      \"display\": {\r\n"
				+ "        \"en-US\": \"replied\"\r\n"
				+ "      },\r\n"
				+ "      \"id\": \"http://id.tincanapi.com/verb/replied\"\r\n"
				+ "    },\r\n"
				+ "    \"object\": {\r\n"
				+ "      \"definition\": {\r\n"
				+ "        \"interactionType\": \"other\",\r\n"
				+ "        \"name\": {\r\n"
				+ "          \"en-US\": \"Re: Weekly Updates - Proposal for a Privacy Management Service\"\r\n"
				+ "        },\r\n"
				+ "        \"description\": {\r\n"
				+ "          \"en-US\": \"<div class=\\\"text_to_html\\\">After a couple of weeks, I've managed to add full authentication and authorisation to my backend and frontend.<br />\\nI'm using our Learning Layers OIDC for all authentication, which falls in line with our current practices.<br />\\nNot to jinx it, but everything seems to be working fine with it.<br />\\nJust one thing: I wasn't able to use the Authorization header for this, but rather used a custom 'access_token' header<br />\\nin my HTTP requests. This is because las2peer requires some special items for this header. Will perhaps fix in future if necessary.<br />\\nNext up is integration with the las2peer network in terms of connecting other services to PCS. Have to start getting<br />\\nconsent checks up and running, as well as passing xAPI statements.</div>\"\r\n"
				+ "        },\r\n"
				+ "        \"type\": \"http://id.tincanapi.com/activitytype/forum-reply\"\r\n"
				+ "      },\r\n"
				+ "      \"id\": \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/discuss.php?d=813#p3201\",\r\n"
				+ "      \"objectType\": \"Activity\"\r\n"
				+ "    }\r\n"
				+ "  },\r\n"
				+ "  \"hasGeneratedId\": true,\r\n"
				+ "  \"deadForwardingQueue\": [],\r\n"
				+ "  \"voided\": false,\r\n"
				+ "  \"verbs\": [\r\n"
				+ "    \"http://id.tincanapi.com/verb/replied\"\r\n"
				+ "  ],\r\n"
				+ "  \"personaIdentifier\": \"5f6b65010656d7981c1ab2d0\",\r\n"
				+ "  \"processingQueues\": [],\r\n"
				+ "  \"person\": {\r\n"
				+ "    \"_id\": \"5f6b650105cd34003fcb7bcb\",\r\n"
				+ "    \"display\": \"Boris Jovanovic\"\r\n"
				+ "  },\r\n"
				+ "  \"__v\": 1,\r\n"
				+ "  \"timestamp\": \"2022-05-25T18:38:41.000Z\",\r\n"
				+ "  \"relatedActivities\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/discuss.php?d=813#p3201\"\r\n"
				+ "  ],\r\n"
				+ "  \"relatedAgents\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/|jovanovic.boris@rwth-aachen.de\",\r\n"
				+ "    \"mailto:hello@learninglocker.net\"\r\n"
				+ "  ],\r\n"
				+ "  \"organisation\": \"5d75242f2ba8a000256244a7\",\r\n"
				+ "  \"_id\": \"628e779990f018008a215588\",\r\n"
				+ "  \"registrations\": [],\r\n"
				+ "  \"pendingForwardingQueue\": []\r\n"
				+ "}";
		return parseAndFormat(raw);
	}
	
	public JSONObject getStatement3() {
		String raw = "{\r\n"
				+ "  \"stored\": \"2022-05-25T18:35:22.305Z\",\r\n"
				+ "  \"active\": true,\r\n"
				+ "  \"completedForwardingQueue\": [],\r\n"
				+ "  \"failedForwardingLog\": [],\r\n"
				+ "  \"client\": \"5f218268c986fb0029f925da\",\r\n"
				+ "  \"lrs_id\": \"5d75244b2ba8a000256244ab\",\r\n"
				+ "  \"completedQueues\": [\r\n"
				+ "    \"STATEMENT_FORWARDING_QUEUE\",\r\n"
				+ "    \"STATEMENT_PERSON_QUEUE\",\r\n"
				+ "    \"STATEMENT_QUERYBUILDERCACHE_QUEUE\"\r\n"
				+ "  ],\r\n"
				+ "  \"activities\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/view.php?id=36\"\r\n"
				+ "  ],\r\n"
				+ "  \"hash\": \"74e8dc132e95b945f213dc126fbfaf5d66aa52ac\",\r\n"
				+ "  \"agents\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/|jovanovic.boris@rwth-aachen.de\"\r\n"
				+ "  ],\r\n"
				+ "  \"statement\": {\r\n"
				+ "    \"authority\": {\r\n"
				+ "      \"objectType\": \"Agent\",\r\n"
				+ "      \"name\": \"New Client\",\r\n"
				+ "      \"mbox\": \"mailto:hello@learninglocker.net\"\r\n"
				+ "    },\r\n"
				+ "    \"stored\": \"2022-05-25T18:35:22.305Z\",\r\n"
				+ "    \"context\": {\r\n"
				+ "      \"extensions\": {\r\n"
				+ "        \"https://tech4comp.de/xapi/context/extensions/actorRoles\": [\r\n"
				+ "          {\r\n"
				+ "            \"roleid\": 1,\r\n"
				+ "            \"rolename\": \"student\"\r\n"
				+ "          }\r\n"
				+ "        ],\r\n"
				+ "        \"https://tech4comp.de/xapi/context/extensions/courseInfo\": {\r\n"
				+ "          \"courseplatform\": \"Moodle\",\r\n"
				+ "          \"courseid\": 11\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    \"actor\": {\r\n"
				+ "      \"name\": \"Boris Jovanovic\",\r\n"
				+ "      \"account\": {\r\n"
				+ "        \"name\": \"jovanovic.boris@rwth-aachen.de\",\r\n"
				+ "        \"homePage\": \"https://moodle.tech4comp.dbis.rwth-aachen.de/\"\r\n"
				+ "      },\r\n"
				+ "      \"objectType\": \"Agent\"\r\n"
				+ "    },\r\n"
				+ "    \"timestamp\": \"2022-05-25T18:34:53Z\",\r\n"
				+ "    \"version\": \"1.0.0\",\r\n"
				+ "    \"id\": \"3118052c-7e35-4acf-ac40-c539f4c7f189\",\r\n"
				+ "    \"verb\": {\r\n"
				+ "      \"display\": {\r\n"
				+ "        \"en-US\": \"viewed\"\r\n"
				+ "      },\r\n"
				+ "      \"id\": \"http://id.tincanapi.com/verb/viewed\"\r\n"
				+ "    },\r\n"
				+ "    \"object\": {\r\n"
				+ "      \"definition\": {\r\n"
				+ "        \"interactionType\": \"other\",\r\n"
				+ "        \"name\": {\r\n"
				+ "          \"en-US\": \"Bachelor/ Master Theses Status Reports\"\r\n"
				+ "        },\r\n"
				+ "        \"type\": \"https://w3id.org/xapi/seriousgames/activity-types/item\"\r\n"
				+ "      },\r\n"
				+ "      \"id\": \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/view.php?id=36\",\r\n"
				+ "      \"objectType\": \"Activity\"\r\n"
				+ "    }\r\n"
				+ "  },\r\n"
				+ "  \"hasGeneratedId\": true,\r\n"
				+ "  \"deadForwardingQueue\": [],\r\n"
				+ "  \"voided\": false,\r\n"
				+ "  \"verbs\": [\r\n"
				+ "    \"http://id.tincanapi.com/verb/viewed\"\r\n"
				+ "  ],\r\n"
				+ "  \"personaIdentifier\": \"5f6b65010656d7981c1ab2d0\",\r\n"
				+ "  \"processingQueues\": [],\r\n"
				+ "  \"person\": {\r\n"
				+ "    \"_id\": \"5f6b650105cd34003fcb7bcb\",\r\n"
				+ "    \"display\": \"Boris Jovanovic\"\r\n"
				+ "  },\r\n"
				+ "  \"__v\": 1,\r\n"
				+ "  \"timestamp\": \"2022-05-25T18:34:53.000Z\",\r\n"
				+ "  \"relatedActivities\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/mod/forum/view.php?id=36\"\r\n"
				+ "  ],\r\n"
				+ "  \"relatedAgents\": [\r\n"
				+ "    \"https://moodle.tech4comp.dbis.rwth-aachen.de/|jovanovic.boris@rwth-aachen.de\",\r\n"
				+ "    \"mailto:hello@learninglocker.net\"\r\n"
				+ "  ],\r\n"
				+ "  \"organisation\": \"5d75242f2ba8a000256244a7\",\r\n"
				+ "  \"_id\": \"628e76eb90f018008a215587\",\r\n"
				+ "  \"registrations\": [],\r\n"
				+ "  \"pendingForwardingQueue\": []\r\n"
				+ "}";
		return parseAndFormat(raw);
	}
}
