package i5.las2peer.services.privacyControlService;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import i5.las2peer.logging.L2pLogger;

public class AuthenticationUtility {
	
	private String userInfoEndpoint;
	private L2pLogger logger;
	private DBUtility db;
	private String firstDPOEmail;
	
	public static enum Role {
		DPO("dpo"),
		MANAGER("manager"),
		STUDENT("student");
		
		public final String label;
		
		Role(String label) {
			this.label = label;
		}
	}
	
	public void configure(
			String userInfoEndpoint,
			L2pLogger logger,
			DBUtility database,
			String firstDPOEmail
			) {
		this.userInfoEndpoint = userInfoEndpoint;
		this.logger = logger;
		this.db = database;
		this.firstDPOEmail = firstDPOEmail;
		
		makeSureDatabaseHasDPO();
	}
	
	private boolean makeSureDatabaseHasDPO() {
		List<String> dpoEmails = db.SelectAllDPOs();
		if (dpoEmails == null) {
			logger.severe("Database not initialised, authentication not possible.");
			//return false; fked if this happens, means someone didnt run the db setup script
		}
		
		if (!dpoEmails.isEmpty()) {
			return true;
		}
		
		db.InsertDPO(firstDPOEmail);
		return false;
	}
	
	/**
	 * This function is used to check if the user access token is valid.
	 * It does this by making a call to the OIDC provider to request user details.
	 * A successful check will return the user's details.
	 * @param token The OAuth2 access token in the form of 'Bearer token'.
	 * @return User's email if token is valid, otherwise null.
	 */
	public String checkUserToken(String token) {
		// Make call to OIDC
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(userInfoEndpoint);
		Invocation.Builder invBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invBuilder.header(HttpHeaders.AUTHORIZATION, token);
		Response response = invBuilder.get();
		String oidcResponse = response.readEntity(String.class);
		if (oidcResponse == null || oidcResponse.isEmpty()) {
			return null;
		}
		
		// Parse string and get email;
		JSONTokener tokener = new JSONTokener(oidcResponse);
		String userEmail = null;
		try {
			JSONObject userInfoJSON = new JSONObject(tokener);
			//logger.info(userInfoJSON.toString());
			userEmail = userInfoJSON.getString("email");
		} catch (JSONException e) {
			logger.warning("Error while authenticating user token: "
					+ "Could not get email from OIDC response.");
			return null;
		}
		
		return userEmail;
	}
	
	
	public boolean checkIfUserHasRole(String userID, Role role) {
		JSONObject userRoleJSON = null;
		switch(role) {
		case DPO:
			userRoleJSON = db.SelectDPO(userID);
			break;
		case MANAGER:
			userRoleJSON = db.SelectManager(userID);
			break;
		case STUDENT:
			userRoleJSON = db.SelectStudent(userID);
			break;
		}
		
		if (userRoleJSON == null || userRoleJSON.isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public JSONArray getAllRoles(String userID) {
		JSONArray retVal = new JSONArray();
		if (checkIfUserHasRole(userID, Role.DPO)) {
			retVal.put("dpo");
		}
		if (checkIfUserHasRole(userID, Role.MANAGER)) {
			retVal.put("manager");
		}
		if (checkIfUserHasRole(userID, Role.STUDENT)) {
			retVal.put("student");
		}
		return retVal;
	}

}
