package i5.las2peer.services.privacy_control_service;

import java.io.InputStream;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.checkerframework.common.reflection.qual.GetClass;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;

import com.google.common.hash.HashCode;

import i5.las2peer.api.Context;
import i5.las2peer.api.ManualDeployment;
import i5.las2peer.api.security.Agent;
import i5.las2peer.api.security.ServiceAgent;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.p2p.EthereumNode;
import i5.las2peer.registry.ReadWriteRegistryClient;
import i5.las2peer.registry.Util;
import i5.las2peer.registry.exceptions.EthereumException;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.security.ServiceAgentImpl;
import i5.las2peer.services.privacy_control_service.AuthenticationUtility.Role;
import i5.las2peer.services.privacy_control_service.smart_contracts.DataProcessingPurposes;
import i5.las2peer.services.privacy_control_service.smart_contracts.PrivacyConsentRegistry;
import i5.las2peer.services.privacy_control_service.smart_contracts.XAPIVerificationRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import model.Course;
import model.Manager;
import model.Purpose;
import model.Service;
import model.Student;

// TODO Describe your own service
/**
 * The Privacy Control Service serves as a safeguard and checkpoint for user
 * data being transmitted in the tech4comp project. It's two main features are
 * consent handling and pseudonymisation.
 * 
 */
// TODO Adjust the following configuration
@Api
@SwaggerDefinition(info = @Info(title = "Privacy Control Service", version = "0.1.0", description = "The Privacy Control Service serves as a safeguard and checkpoint for "
		+ "user data being transmitted in the tech4comp project.",
		// termsOfService = "http://your-terms-of-service-url.com",
		contact = @Contact(name = "Boris Jovanovic", email = "jovanovic.boris@rwth-aachen.de"), license = @License(name = "your software license name", url = "http://your-software-license-url.com")))
@ManualDeployment
@ServicePath("/pieces")
public class PrivacyControlService extends RESTService {

	public final static L2pLogger logger = L2pLogger.getInstance(PrivacyControlService.class.getName());
	
	public static final String AUTHENTICATION_HEADER_NAME = "access_token";
	
	private String OIDC_USER_INFO_ENDPOINT;
	private String FIRST_DPO_EMAIL;
	
	private String DB_URL;
	private int DB_PORT;
	private String DB_NAME;
	private String DB_USERNAME;
	private String DB_PASSWORD;
	
	private String LAS2PEER_GET_SERVICES_ENDPOINT;
	
	private String LRS_ACTOR_ACCOUNT_HOMEPAGE;
	private String LRS_STATEMENT_QUERY_ENDPOINT;
	private String LRS_STATEMENT_DELETE_ENDPOINT;
	private String LRS_CLIENT_AUTHORISATION;

	private static ReadWriteRegistryClient registryClient;
	private static PrivacyConsentRegistry consentRegistry;
	private static DataProcessingPurposes purposesRegistry;
	private static XAPIVerificationRegistry verificationRegisrty;

	private static DBUtility database;
	private static AuthenticationUtility auth;
	private static StatementUtility statUtil;

	private static boolean serviceInitialised = false;

	public PrivacyControlService() {
		setFieldValues();
	}
	
	@GET
	@Path("/dbcontry")
	@Produces(MediaType.TEXT_PLAIN)
	public Response tryDBConnect() {
		database = new DBUtility();
		boolean dbflag = database.establishPostgresConnection(DB_URL, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD);
		if (!dbflag) {
			PrivacyControlService.logger.severe("Could not connect to PrivacyControlService database.");
			return Response.serverError().entity("Error while connecting to service's database").build();
		}
		
		database.setupDatabaseTables();
		
		return Response.ok("All good").build();
	}
	
	@POST
	@Path("/init")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(value = { @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK") })
	public Response init(ContainerRequestContext context) {
		if (serviceInitialised) {
			return Response.ok("Service already initialised.").build();
		}
		
		logger.info(context.getHeaderString("access_token"));
		// TODO: Authenticate the person doing the init. Can't do it with DPO check because of timeline.

		L2pLogger.setGlobalConsoleLevel(Level.INFO);
		logger.info("Initializing service...");

		ServiceAgentImpl agent = (ServiceAgentImpl) Context.getCurrent().getServiceAgent();
		registryClient = ((EthereumNode) agent.getRunningAtNode()).getRegistryClient();
		// TODO: Put this in environment variables.
		consentRegistry = registryClient.loadSmartContract(
				PrivacyConsentRegistry.class,
				"0xC58238a482e929584783d13A684f56Ca5249243E");
		purposesRegistry = registryClient.loadSmartContract(
				DataProcessingPurposes.class,
				"0x2cCEc92848aA65A79dEa527B0449e4ce6f86472c");
		verificationRegisrty = registryClient.loadSmartContract(
				XAPIVerificationRegistry.class,
				"0x5b3F42bDD268D21DefF2986F182c5646B35afBae");

		if (consentRegistry == null) {
			logger.severe("Could not load PrivacyConsentRegistry smart contract.");
			return Response.serverError().entity("Error getting contract: PrivacyConsentRegistry.").build();
		}
		if (purposesRegistry == null) {
			logger.severe("Could not load DataProcessingPurposes smart contract.");
			return Response.serverError().entity("Error getting contract: DataProcessingPurposes.").build();
		}
		if (verificationRegisrty == null) {
			logger.severe("Could not load XAPIVerificationRegistry smart contract.");
			return Response.serverError().entity("Error getting contract: XAPIVerificationRegistry.").build();
		}
		logger.info("Blockchain registries loaded.");

		database = new DBUtility();
		boolean dbflag = database.establishPostgresConnection(DB_URL, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD);
		if (!dbflag) {
			PrivacyControlService.logger.severe("Could not connect to PrivacyControlService database.");
			return Response.serverError().entity("Error while connecting to service's database").build();
		}
		dbflag = database.prepareStatements();
		if (!dbflag) {
			PrivacyControlService.logger.severe("Could not connect prepare database statements.");
			return Response.serverError().entity("Database prepared statement error.").build();

		}
		logger.info("Database connection established.");
		
		// Authentication cannot happen before db connection is established.
		auth = new AuthenticationUtility();
		auth.configure(OIDC_USER_INFO_ENDPOINT, logger, database, FIRST_DPO_EMAIL);
		logger.info("Authentication utility configured.");

		statUtil = new StatementUtility();
		
		serviceInitialised = true;
		logger.info("Done.");
		return Response.ok(200).entity("Initialisation was successful.").build();
	}
	
	@GET
	@Path("/init")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInit() {
		JSONObject retVal = new JSONObject();
		retVal.put("initialised", serviceInitialised);
		return Response.ok().entity(retVal.toString()).build();
	}
	
	@GET
	@Path("/user-roles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserRoles(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		logger.warning("HEADER NAME: " + AUTHENTICATION_HEADER_NAME);
		logger.warning("ToKeN Is: " + access_token);
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		
		JSONArray roles = auth.getAllRoles(userID);
		return Response.ok().entity(roles.toString()).build();
	}
	
	@GET
	@Path("/test-headers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testHeaders(@javax.ws.rs.core.Context HttpHeaders headers) {
		MultivaluedMap<String,String> head_map = headers.getRequestHeaders();
		Set<String> head_set = head_map.keySet();
		
		JSONObject retVal = new JSONObject();
		for (String header : head_set) {
			retVal.put(header, head_map.get(header).get(0));
		}
		
		return Response.ok(retVal.toString()).build();
	}
		

	/////////////////////////////////////////////////////////////////////////////////
	///                          CRUD (mostly from frontend)                      ///
	/////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/register/manager")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerManager(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			Manager manager) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!auth.checkIfUserHasRole(userID, Role.DPO)) {
			return Response.status(403).build();
		}
		
		
		int result = database.InsertManager(manager);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();

		}

		String responseMessage = "Manager " + manager.getName() + " succesfully registered.";
		return Response.ok(responseMessage).build();
	}
	
	@GET
	@Path("/managers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getManagers(@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!auth.checkIfUserHasRole(userID, Role.DPO)) {
			return Response.status(403).build();
		}
		
		JSONArray retVal = database.SelectAllManagers();		
		
		return Response.ok().entity(retVal.toString()).build();
	}

	// TODO: Service registry integrations. Will receive ID, but need to search service registry to create service in DB.
	@POST
	@Path("/register/service")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerService(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			Service service) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!auth.checkIfUserHasRole(userID, Role.DPO)) {
			return Response.status(403).build();
		}
		
		int result = database.InsertService(service);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();
		}

		String responseMessage = "Service " + service.getName() + " succesfully registered.";
		return Response.ok(responseMessage).build();
	}

	@GET
	@Path("/service/{serviceID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getService(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "serviceID") String serviceID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		boolean roleDPO = auth.checkIfUserHasRole(userID, Role.DPO);
		boolean roleManager = auth.checkIfUserHasRole(userID, Role.MANAGER);
		if (!(roleDPO || roleManager)) {
			return Response.status(403).build();
		}
				
		JSONObject retVal = database.SelectServiceWithCourses(serviceID);
		if (retVal == null) {
			return Response.serverError().entity("Database error.").build();
		}
		if (retVal.isEmpty()) {
			return Response.status(404).entity("Service not found.").build();
		}
		
		// If the user is a manager, but not the manager of this service, deny request
		if (!roleDPO && roleManager) {
			if (!userID.equals(retVal.getString("managerID"))) {
				return Response.status(403).build();
			}
		}

		return Response.ok().entity(retVal.toString()).build();
	}

	@GET
	@Path("/manager/{managerid}/services")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getManagerServices(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "managerid") String managerID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(userID, Role.DPO) || auth.checkIfUserHasRole(userID, Role.MANAGER))) {
			return Response.status(403).build();
		}
		
		// If manager, has to be the same as user
		if (auth.checkIfUserHasRole(userID, Role.MANAGER)) {
			if (!userID.equals(managerID)) {
				return Response.status(403).build();
			}
		}
		
		JSONArray retVal = database.SelectManagerServices(managerID);
		if (retVal == null) {
			return Response.serverError().entity("Database error.").build();
		}

		return Response.ok().entity(retVal.toString()).build();
	}

	@POST
	@Path("/register/course")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerCourse(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			Course course) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(userID, Role.MANAGER))) {
			return Response.status(403).build();
		}
		
		// If user is not manager of service, deny request
		String serviceID = course.getServiceID();
		logger.info(course.toString());
		JSONObject serviceJSON = database.SelectService(serviceID);
		logger.info(serviceJSON.toString());
		if(serviceJSON.isEmpty()) {
			return Response.status(400).build();
		}
		
		if (!userID.equals(serviceJSON.getString("managerID"))) {
			return Response.status(403).build();
		}
		
		
		int result = database.InsertCourse(course);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();
		}

		String responseMessage = "Course " + course.getName() + " succesfully registered.";
		return Response.ok(responseMessage).build();
	}

	@GET
	@Path("/service/{serviceID}/course/{courseID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCourse(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "serviceID") String serviceID,
			@PathParam(value = "courseID") String courseID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		boolean hasRoleManager = auth.checkIfUserHasRole(userID, Role.MANAGER);
		boolean hasRoleStudent = auth.checkIfUserHasRole(userID, Role.STUDENT);
		if (!(hasRoleManager || hasRoleStudent)) {
			return Response.status(403).build();
		}
		
		boolean shouldBeDenied = true;
		// If the user is a manager, they have to be the service's manager
		if (hasRoleManager) {
			JSONObject serviceJSON = database.SelectService(serviceID);
			if (userID.equals(serviceJSON.getString("managerID"))) {
				shouldBeDenied = false;
			}
		}
		
		// If is a student, has to be enrolled in the course
		if (hasRoleStudent) {
			// TODO: This can be done in a SQL query
			JSONArray studentsJSON = database.SelectStudentsInCourse(serviceID, courseID);
			for (Object element : studentsJSON) {
				JSONObject studentJSON = (JSONObject) element;
				String studentID = studentJSON.getString("email");
				if (userID.equals(studentID)) {
					shouldBeDenied = false;
				}
			}
		}
		
		// If the user is both student and manager, any of the above are OK
		if (shouldBeDenied) {
			return Response.status(403).build();
		}
		
		JSONObject retVal = database.SelectCourse(serviceID, courseID);
		if (retVal == null) {
			return Response.serverError().entity("Database error.").build();
		}
		if (retVal.isEmpty()) {
			return Response.status(404).entity("Course not found.").build();
		}

		return Response.ok().entity(retVal.toString()).build();
	}

	@PUT
	@Path("/service/{serviceID}/course/{courseID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response editCourse(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			Course course) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(userID, Role.MANAGER))) {
			return Response.status(403).build();
		}
		
		// If user is not manager of service, deny request
		String serviceID = course.getServiceID();
		JSONObject serviceJSON = database.SelectService(serviceID);
		if (!userID.equals(serviceJSON.getString("managerID"))) {
			return Response.status(403).build();
		}
		
		int result = database.UpdateCourse(course);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();
		}

		String responseMessage = "Course " + course.getName() + " succesfully updated.";
		return Response.ok(responseMessage).build();
	}

	@POST
	@Path("/register/student")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerStudent(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			Student student) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(userID, Role.MANAGER))) {
			return Response.status(403).build();
		}
		
		int result = database.InsertStudent(student);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();
		}

		String responseMessage = "Student " + student.getEmail() + " succesfully registered.";
		return Response.ok(responseMessage).build();
	}

	// PURPOSE IN COURSE
	
	@POST
	@Path("/register/purpose-in-course")
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerPurposesInCourse(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			InputStream input) {
		
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(userID, Role.MANAGER))) {
			return Response.status(403).build();
		}
		
		JSONTokener tokener = new JSONTokener(input);
		JSONObject picJSON = null;
		try {
			picJSON = new JSONObject(tokener);
		} catch (JSONException e) {
			logger.warning("Could not parse JSON in POST purpose in course request.");
			return Response.status(400).entity("Invalid JSON.").build();
		}
		
		String courseID;
		String serviceID;
		List<Integer> purposeIDs = new ArrayList<Integer>();
		try {
			courseID = picJSON.getString("courseID");
			serviceID = picJSON.getString("serviceID");
			for (Object x : picJSON.getJSONArray("purposes").toList()) {
				purposeIDs.add((int) x);
			}
		} catch (JSONException e) {
			logger.warning("Mandatory fields not found in request JSON.");
			e.printStackTrace();
			return Response.status(400).entity("Invalid JSON.").build();
		} catch (ClassCastException e) {
			logger.warning("Purpose list contains non-integer ids.");
			return Response.status(400).entity("Invalid JSON.").build();
		}
		
		// If user is not manager of service, deny request
		JSONObject serviceJSON = database.SelectService(serviceID);
		if (!userID.equals(serviceJSON.getString("managerID"))) {
			return Response.status(403).build();
		}
		
		// First delete current registration
		int resultDelete = database.DeletePurposesInCourse(serviceID, courseID);
		if (resultDelete < 0) {
			return Response.serverError().entity("Database error.").build();
		}
		
		// Then add new list
		int[] result = database.InsertPurposesInCourse(purposeIDs, serviceID, courseID);
		if (result == null) {
			return Response.serverError().entity("Database error.").build();
		}
		
		String responseMessage = "Purposes succesfully added to course with ID " + serviceID
				+ "|" + courseID;
		return Response.ok(responseMessage).build();
	}
	
	@GET
	@Path("/purpose-in-course/{serviceid}/{courseid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposesInCourse(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {
		
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		
		List<Integer> result = database.SelectPurposesInCourse(serviceID, courseID);
		if (result == null) {
			return Response.serverError().entity("Database error").build();
		}
		
		JSONArray retVal = new JSONArray();
		for (int id : result) {
			Purpose purpose = getPurposeBlockchain(id);
			if (purpose != null) {
				retVal.put(purpose.toJSON());
			}
			else {
				logger.severe("Error while retrieving purpose with ID=" + id + ". "
						+ "Possible incosistency of blockchain and database.");
			}
		}
		
		return Response.ok().entity(retVal.toString()).build();
	}

	// STUDENT IN COURSE
	
	@GET
	@Path("/student-in-course/{serviceid}/{courseid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentsInCourse(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {
		
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(userID, Role.MANAGER))) {
			return Response.status(403).build();
		}
		
		// If user is not manager of service, deny request
		JSONObject serviceJSON = database.SelectService(serviceID);
		if (!userID.equals(serviceJSON.getString("managerID"))) {
			return Response.status(403).build();
		}
		
		JSONArray result = database.SelectStudentsInCourse(serviceID, courseID);
		if (result == null) {
			return Response.serverError().entity("Database error").build();
		}
		
		return Response.ok().entity(result.toString()).build();
	}
	
	@POST
	@Path("/register/student-in-course/{serviceid}/{courseid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerStudentInCourse(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID,
			Student student) {
		
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(userID, Role.MANAGER))) {
			return Response.status(403).build();
		}
		
		// If user is not manager of service, deny request
		JSONObject serviceJSON = database.SelectService(serviceID);
		if (!userID.equals(serviceJSON.getString("managerID"))) {
			return Response.status(403).build();
		}
		
		// See if user exists in DB
		JSONObject resultStudent = database.SelectStudent(student.getEmail());
		if (resultStudent.isEmpty()) {
			// Not in DB, so have to add
			int result = database.InsertStudent(student);
			if (result <= 0) {
				return Response.serverError().entity("Database error.").build();
			}
			logger.info("Created " + student.toString());
		}
		
		
		// First create hash
		String pseudonym = HashUtility.hashWithRandomSalt(student.getEmail());
		
		// Then enter into DB
		int result = database.InsertStudentInCourse(student.getEmail(), serviceID, courseID, pseudonym);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();
		}
		
		String responseMessage = "Student with ID " + student.getEmail() + " succesfully added to course with ID " + serviceID
				+ "|" + courseID;
		return Response.ok(responseMessage).build();
	}
	
	@GET
	@Path("/student-in-course/{serviceid}/{courseid}/pseudonym")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentInCoursePseudonym(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID
			) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String tokenUserID = auth.checkUserToken(access_token);
		if (tokenUserID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(tokenUserID, Role.STUDENT))) {
			return Response.status(403).build();
		}
		
		String studentID = tokenUserID;
		// Get pseudonym
		String studentPseudonym = database.SelectStudentPseudonym(studentID, serviceID, courseID);
		if (studentPseudonym == null) {
			return Response.status(400).build();
		}
		
		JSONObject retVal = new JSONObject();
		retVal.put("pseudonym", studentPseudonym);
		
		return Response.ok().entity(retVal.toString()).build();
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////
	///                                Consent                                    ///
	/////////////////////////////////////////////////////////////////////////////////
	
	@GET
	@Path("/consent/{studentid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentConsentOverview(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "studentid") String studentID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(userID, Role.STUDENT))) {
			return Response.status(403).build();
		}
		
		if (!userID.equals(studentID)) {
			return Response.status(403).build();
		}
		
		JSONArray result = database.SelectCoursesWithStudent(studentID);
		
		return Response.ok().entity(result.toString()).build();
	}

	@GET
	@Path("/consent/{userid}/{serviceid}/{courseid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplate(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "userid") String userID,
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {

		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String tokenUserID = auth.checkUserToken(access_token);
		if (tokenUserID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(tokenUserID, Role.STUDENT))) {
			return Response.status(403).build();
		}
		
		// If user is not the same as student request, deny
		if (!userID.equals(tokenUserID)) {
			return Response.status(403).build();
		}
		
		try {
			JSONObject retVal = new JSONObject();
			retVal.put("userID", userID);
			retVal.put("serviceID", serviceID);
			retVal.put("courseID", courseID);
			
			Tuple3<List<Integer>, List<Integer>, BigInteger> tuple = 
					getConsent(userID, serviceID, courseID);
			
			List<Integer> purposeIDs = tuple.component1();
			List<Integer> purposeVersions = tuple.component2();
			BigInteger timestamp = tuple.component3();
			
			JSONArray purposeIDsJSON = new JSONArray();
			JSONArray purposeVersionsJSON = new JSONArray();
			for (int i = 0; i < purposeIDs.size(); i++) {
				purposeIDsJSON.put(purposeIDs.get(i));
				purposeVersionsJSON.put(purposeVersions.get(i));
			}
			retVal.put("purposeIDs", purposeIDsJSON);
			retVal.put("purposeVersions", purposeVersionsJSON);
			
			retVal.put("timestamp", timestamp.intValue());

			return Response.ok().entity(retVal.toString()).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return Response.serverError().build();
		}

	}
	
	
	/**
	 * Helper method to retrieve a user's consent. The resulting purposes hold only the
	 * values of purposes which are current, i.e. their version is the same as the current
	 * version in the DataPurposesRegistry.
	 * 
	 * @param userID ID (email) of the user.
	 * @param serviceID ID (las2peer service name) of the service.
	 * @param courseID Internal ID that service uses to identify course.
	 * @return A tuple consisting of, in order: 
	 * <ol>
	 * 	<li> A list of ID's of the purposes for which the user has given consent, wrt. course and service. </li>
	 * 	<li> The versions of the purposes, which IDs are in the first list, ordered the same way. </li>
	 * 	<li> The timestamp of when the consent was given. </li>
	 * </ol>
	 * @throws Exception if the DataPurposesRegistry or PrivacyConsentRegistry couldn't be reached.
	 */
	private Tuple3<List<Integer>, List<Integer>, BigInteger> getConsent(
			String userID,
			String serviceID,
			String courseID) throws Exception {
		
		logger.info("Attempting to get consent of: User["
						+ userID + "] Service["
						+ serviceID + "] Course["
						+ courseID +"]");
		byte[] bUserID = HashUtility.hashForBlockchainByte(userID);
		byte[] bServiceID = HashUtility.hashForBlockchainByte(serviceID);
		byte[] bCourseID = HashUtility.hashForBlockchainByte(courseID);
		Tuple3<List<BigInteger>, List<BigInteger>, BigInteger> tmpTuple = consentRegistry
				.getConsentInfo(bUserID, bServiceID, bCourseID).send();
		logger.info("Tupple info: " + tmpTuple.component1().toString() + " " + tmpTuple.component2() + " "
				+ tmpTuple.component3());
		
		// Convert consent purpose versions from BigInteger to Integer and put in array
		List<Integer> consentPurposeVersions = new ArrayList<Integer>();
		for (BigInteger bi : tmpTuple.component2()) {
			consentPurposeVersions.add(bi.intValue());
		}
		
		// Get current versions of mentioned purposes
		List<Purpose> purposesInPurposeRegistry = new ArrayList<Purpose>();
		for (BigInteger bi : tmpTuple.component1()) {
			purposesInPurposeRegistry.add(getPurposeBlockchain(bi.intValue()));
		}
		
		// Check if purpose version in consent is up to date
		List<Integer> validPurposeIDs = new ArrayList<Integer>();
		List<Integer> validPurposeVersions = new ArrayList<Integer>();
		for (int i = 0; i < consentPurposeVersions.size(); i++) {
			Purpose tmp = purposesInPurposeRegistry.get(i);
			// Only add those whose version is current
			if (consentPurposeVersions.get(i) == tmp.getVersion()) {
				validPurposeIDs.add(tmp.getId());
				validPurposeVersions.add(tmp.getVersion());
			}
		}
		
		BigInteger timestamp = tmpTuple.component3();
		
		Tuple3<List<Integer>, List<Integer>, BigInteger> retVal = 
				new Tuple3<List<Integer>, List<Integer>, BigInteger>(validPurposeIDs, validPurposeVersions, timestamp); 
		return retVal;
	}
	
	
	@POST
	@Path("/consent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postStudentConsent(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			InputStream input) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		JSONTokener tokener = new JSONTokener(input);
		JSONObject consentJSON = null;
		try {
			consentJSON = new JSONObject(tokener);
		} catch (JSONException e) {
			logger.warning("Could not parse JSON in POST Consent request.");
			return Response.status(400).entity("Invalid JSON.").build();
		}
		
		String studentID = null;
		String courseID = null;
		String serviceID = null;
		JSONArray purposesJSON = new JSONArray();
		
		try {
			studentID = consentJSON.getString("studentID");
			courseID = consentJSON.getString("courseID");
			serviceID = consentJSON.getString("serviceID");
			purposesJSON = consentJSON.getJSONArray("purposes");
		} catch (JSONException e) {
			logger.warning("Could not retrieve needed JSON item in POST Consent.");
			return Response.status(400).entity("Mandatory item not included").build();
		}
		// Just making sure
		if (studentID == null || courseID == null || serviceID == null) {
			return Response.status(400).entity("Mandatory item not included").build();
		}
		
		// Authentication and authorisation
		String tokenUserID = auth.checkUserToken(access_token);
		if (tokenUserID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(tokenUserID, Role.STUDENT))) {
			return Response.status(403).build();
		}
		
		// If user ID is not the same as the student ID, deny
		if (!tokenUserID.equals(studentID)) {
			return Response.status(403).build();
		}
		
		List<BigInteger> purposes = new ArrayList<BigInteger>();
		for (int i = 0; i < purposesJSON.length(); i++) {
			int purpose = purposesJSON.getInt(i);
			purposes.add(BigInteger.valueOf(purpose));
		}
		
		// TODO: Check in DB if student attends course, etc.
		
		byte[] bStudentID = HashUtility.hashForBlockchainByte(studentID);
		byte[] bCourseID = HashUtility.hashForBlockchainByte(courseID);
		byte[] bServiceID = HashUtility.hashForBlockchainByte(serviceID);
		
		logger.info("Attempting to store consent for student: " + studentID);
		
		List<BigInteger> purposeVersions = new ArrayList<BigInteger>();
		for (BigInteger bi : purposes) {
			Purpose tmp = getPurposeBlockchain(bi.intValue());
			purposeVersions.add(BigInteger.valueOf(tmp.getVersion()));
		}
		logger.info("Retrieved purpose versions.");
		if (purposeVersions.contains(BigInteger.ZERO)) {
			logger.warning("Consent in request contains unregistered purpose. Aborting.");
			return Response.status(400).entity("Consent contains unknown purpose ID.").build();
		}
		
		try {
			consentRegistry.storeConsent(bStudentID, bServiceID, bCourseID, purposes, purposeVersions).send();
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe("Could not store on blockchain.");
			return Response.serverError().entity("Could not store on blockchain").build();
		}
		logger.info("Done.");		
		
		return Response.ok("Consent stored.").build();
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	///                            Collected data                                 ///
	/////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Helper method which makes a call to the blockchain to verify that given statements
	 * have been previously stored in the xAPIVerificationRegistry as hashes.
	 * 
	 * @param userID ID (email) of the user.
	 * @param statements JSON array containing xAPI Statements (as JSON objects) that should be verified.
	 * @return A JSON array containing the same statements as given, with a boolean value
	 * indication if the statement is verified or not.
	 * @throws Exception If there is a problem with reaching the blockchain and/or the registry.
	 */
	private JSONArray verifyStatements(
			String userID,
			JSONArray statements
			) {
		
		byte[] bUserID = HashUtility.hashForBlockchainByte(userID);
		Tuple4<List<byte[]>, List<BigInteger>, List<BigInteger>, List<BigInteger>> tuple;
		try {
			tuple = verificationRegisrty.getLogEntries(bUserID).send();
		} catch (Exception e) {
			logger.severe("Could not retrieve data from xAPI Verification Registry during verifyStatements.");
			return null;
		}
		
		List<byte[]> dataHashes = tuple.component1();
		logger.info("[");
		for (byte[] dataHash : dataHashes) {
			logger.info(HashCode.fromBytes(dataHash) + ",");
		}
		logger.info("]");
		
		JSONArray retVal = new JSONArray();
		for (int i = 0; i < statements.length(); i++) {
			JSONObject statement = statements.getJSONObject(i);
			JSONObject coreStatement = statUtil.extractCoreStatement(statement);
			byte[] hashedStatement = HashUtility.hashForBlockchainByte(coreStatement.toString());
			logger.info("Hashed statement: " + HashCode.fromBytes(hashedStatement));
			
			boolean verified = false;
			// Cannot directly compare byte arrays for some reason
			for(byte[] item : dataHashes) {
				if (HashUtility.compareHashBytes(hashedStatement, item)) {
					verified = true;
				}
			}
			
			JSONObject entry = new JSONObject();
			entry.put("statement", statement);
			entry.put("verified", verified);
			retVal.put(entry);
		}
		
		return retVal;
	}
	
	private JSONArray processCollectedDataResponse(JSONArray verifiedStatements) {
		JSONArray retVal = new JSONArray();
		for (Object item : verifiedStatements) {
			JSONObject itemJSON = (JSONObject) item;
			JSONObject statement = itemJSON.getJSONObject("statement");
			String timestamp = statement.getString("timestamp");
			String verbName = statUtil.getVerbDisplayName(statement);
			String objectName = statUtil.getObjectName(statement);
			itemJSON.put("timestamp", timestamp);
			itemJSON.put("verb", verbName);
			itemJSON.put("object", objectName);
			retVal.put(itemJSON);
		}
		return retVal;
	}
	
	@GET
	@Path("/collected-data/{serviceid}/{courseid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCollectedData(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String tokenUserID = auth.checkUserToken(access_token);
		if (tokenUserID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(tokenUserID, Role.STUDENT))) {
			return Response.status(403).build();
		}
		
		String studentID = tokenUserID;
		// Get pseudonym
		String studentPseudonym = database.SelectStudentPseudonym(studentID, serviceID, courseID);
		if (studentPseudonym == null) {
			return Response.status(400).build();
		}
		JSONArray lrsStatements = retrieveStatementsFromLRS(studentPseudonym);
		
		JSONArray verifiedStatements = verifyStatements(studentID, lrsStatements);
		JSONArray processedStatements = processCollectedDataResponse(verifiedStatements);
		
		return Response.ok().entity(processedStatements.toString()).build();
	}
	
	@DELETE
	@Path("/collected-data/{serviceid}/{courseid}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteCollectedData(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String tokenUserID = auth.checkUserToken(access_token);
		if (tokenUserID == null) {
			return Response.status(401).build();
		}
		if (!(auth.checkIfUserHasRole(tokenUserID, Role.STUDENT))) {
			return Response.status(403).build();
		}
		
		String studentID = tokenUserID;
		// Get pseudonym
		String studentPseudonym = database.SelectStudentPseudonym(studentID, serviceID, courseID);
		if (studentPseudonym == null) {
			return Response.status(400).build();
		}
		
		boolean result = deleteAllStudentPseudonymStatementsFromLRS(studentPseudonym);
		if (result) {
			return Response.ok().build();
		} else {
			return Response.serverError().entity("Could not reach LRS for deletion.").build();
		}
		
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	///                                Purpose                                    ///
	/////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Helper method to retrieve Purpose data from the DataProcessingPurposes blockchain
	 * registry.
	 * 
	 * @param purposeID ID (code) of the purpose.
	 * @return Purpose class object if the purpose exists in the blockchain, otherwise null.
	 */
	private Purpose getPurposeBlockchain(int purposeID) {
		
		logger.info("Attempting to get purpose with ID: " + purposeID);
		try {
			Tuple3<String, String, BigInteger> tuple = purposesRegistry.getPurpose(BigInteger.valueOf(purposeID))
					.send();
			Purpose purpose = new Purpose(purposeID, tuple.component1(), tuple.component2(),
					tuple.component3().intValue());
			logger.info("Got Purpose: " + purpose);
			return purpose;
		} catch (Exception e) {
			logger.severe("Error while trying to get purpose list item.");
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/purpose-list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposeList(@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		String tokenUserID = auth.checkUserToken(access_token);
		if (tokenUserID == null) {
			return Response.status(401).build();
		}
		
		logger.info("Attempting to retrieve list of purpose IDs...");
		List<BigInteger> purposeIDs;
		try {
			purposeIDs = purposesRegistry.getAllPurposeIDs().send();
		} catch (Exception e) {
			logger.severe("Could not retrieve purpose ID list.");
			return Response.serverError().build();
		}
		logger.info("Received list: " + purposeIDs);

		JSONArray retVal = new JSONArray();

		logger.info("Retrieving individual purposes...");
		for (BigInteger bi : purposeIDs) {
			try {
				Tuple3<String, String, BigInteger> tuple = purposesRegistry.getPurpose(bi).send();
				JSONObject tmp = new JSONObject();
				tmp.put("id", bi.intValue());
				tmp.put("title", tuple.component1());
				tmp.put("description", tuple.component2());
				tmp.put("version", tuple.component3());
				retVal.put(tmp);
			} catch (Exception e) {
				logger.severe("Could not retrieve purpose for ID: " + bi.intValue());
			}
			logger.info("Successfully retrieved purpose with ID: " + bi.intValue());
		}

		logger.info("Done.");
		return Response.ok(retVal.toString()).build();
	}

	@GET
	@Path("/purpose-list/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposeItem(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			@PathParam(value = "id") int purposeID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		String tokenUserID = auth.checkUserToken(access_token);
		if (tokenUserID == null) {
			return Response.status(401).build();
		}
		
		Purpose retVal = getPurposeBlockchain(purposeID);
		if (retVal != null) {
			return Response.ok(retVal.toJSON().toString()).build();
		}
		else {
			return Response.serverError().build();
		}
	}

	@POST
	@Path("/purpose-list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postPurpose(
			@HeaderParam(AUTHENTICATION_HEADER_NAME) String access_token,
			Purpose purpose) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		// Authentication and authorisation
		String userID = auth.checkUserToken(access_token);
		if (userID == null) {
			return Response.status(401).build();
		}
		if (!auth.checkIfUserHasRole(userID, Role.DPO)) {
			return Response.status(403).build();
		}
		
		logger.info("Attempting to create or modify purpose: " + purpose);
		BigInteger id = BigInteger.valueOf(purpose.getId());
		String title = purpose.getTitle();
		String description = purpose.getDescription();

		try {
			purposesRegistry.createOrModifyPurpose(id, title, description).send();
		} catch (Exception e) {
			logger.severe("Error when accessing smart contract, could not create or modify purpose.");
			e.printStackTrace();
			return Response.serverError().build();
		}
		
		logger.info("Successfuly committed purpose to blockchain, now committing to database...");
		
		// If not in DB insert, else update
		JSONObject purposeInDB = database.SelectPurpose(purpose.getId());
		if (purposeInDB.isEmpty()) {
			int result = database.InsertPurpose(purpose);
			if (result <= 0) {
				logger.severe("Could not insert Purpose: " + purpose.toString() + " into database.");
				return Response.serverError().entity("Database error.").build();
			}
		}
		else {
			int result = database.UpdatePurpose(purpose);
			if (result <= 0) {
				logger.severe("Could not update Purpose: " + purpose.toString() + " in database.");
				return Response.serverError().entity("Database error.").build();
			}
		}

		logger.info("Done.");
		return Response.ok().build();
	}	
	
	
	/////////////////////////////////////////////////////////////////////////////////
	///                                Incoming RMI                               ///
	/////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * This method facilitates the Data Processing Request that data collectors, such
	 * as data proxies, call in order to get information on a users consent data and
	 * pseudonym for the given course.
	 * 
	 * @param studentID ID (email) of the user.
	 * @param courseID The service's internal ID of the course.
	 * @return JSON String containing the purpose IDs of purposes that the user
	 * has given consent to and the user's pseudonym for the course. If the user has
	 * not been enrolled in the course or the service isn't registered or if there is
	 * any other error, null is returned instead.
	 */
	public String dataProcessingRequest(String studentID, String courseID) {
		if (!serviceInitialised) {
			return null;
		}
		
		Agent mainAgent = Context.get().getMainAgent();
		ServiceAgent callerService = null;
		if (mainAgent instanceof ServiceAgent) {
			callerService = (ServiceAgent) mainAgent;
		}
		else {
			// Unknown what should happen if caller isn't a service
			return null;
		}
		String serviceID = callerService.getServiceNameVersion().getName();
		
		logger.info("PCS received Data Processing Request: \n"
				+ "		Service: " + serviceID + "\n"
				+ "		Course: " + courseID + "\n"
				+ "		Student: " + studentID);
		
		// Check if service is registered
		JSONObject serviceJSON = database.SelectService(serviceID);
		if (serviceJSON == null || serviceJSON.isEmpty()) {
			logger.warning("Unregistered caller service in PCS Data Processing Request. "
					+ "Request denied.");
			return null;
		}
		
		// Get user's current consent
		Tuple3<List<Integer>, List<Integer>, BigInteger> consentTuple;
		try {
			consentTuple = getConsent(studentID, serviceID, courseID);
		} catch (Exception e) {
			logger.severe("Blockchain error.");
			return null;
		}
		
		// Add purpose ID array
		JSONObject retVal = new JSONObject();
		JSONArray purposes = new JSONArray();
		for (int purposeID : consentTuple.component1()) {
			purposes.put(purposeID);
		}
		retVal.put("purposes", purposes);
		
		// Get pseudonym
		String pseudonym = database.SelectStudentPseudonym(studentID, serviceID, courseID);
		if (pseudonym == null) {
			logger.warning("PCS Data Processing Request error: Student not registered in service course.");
			return null;
		}
		retVal.put("pseudonym", pseudonym);
		logger.info("Data processing request response is: " + retVal.toString());
		return retVal.toString();
	}
	
	@POST
	@Path("/forward-statement")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response forwardStatement(InputStream input) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		// TODO: get service id from service agent info
		String serviceID = "NEVERPUTSPACE";
		
		JSONTokener tokener = new JSONTokener(input);
		JSONObject entryJSON = null;
		JSONObject statementJSON = null;
		int purposeID;
		String courseID;
		try {
			entryJSON = new JSONObject(tokener);
			statementJSON = entryJSON.getJSONObject("statement");
			purposeID = entryJSON.getInt("purpose");
			courseID = entryJSON.getString("course");
		} catch (JSONException e) {
			//logger.warning("Could not parse JSON in POST Consent request.");
			e.printStackTrace();
			return Response.status(400).entity("Invalid JSON.").build();
		}
		
		String userID = statUtil.getActorEmail(statementJSON);
		if (userID == null) {
			return Response.status(400).entity("Invalid xAPI Statement: Missing actor account email.").build();
		}
		
		// Check user consent
		List<Integer> purposeIDs;
		List<Integer> purposeVersions;
		try {
			Tuple3<List<Integer>, List<Integer>, BigInteger> tuple = 
					getConsent(userID, serviceID, courseID);
			purposeIDs = tuple.component1();
			purposeVersions = tuple.component2();
		} catch (Exception e) {
			return Response.serverError().entity("Blockchain issue.").build();
		}
		
		int purposeIndex = purposeIDs.indexOf(purposeID);
		if (purposeIndex < 0) {
			// The user did not give permission
			return Response.status(403).entity("User did not give consent.").build();
		}
		int purposeVersion = purposeVersions.get(purposeIndex);
		
		logger.info("Attempting to log entry in xAPIVerificationRegistry:");
		// Log to verification registry
		byte[] bUserID = HashUtility.hashForBlockchainByte(userID);
		logger.info("User ID and hash: " + userID + " -> " + HashCode.fromBytes(bUserID));
		String data = statUtil.extractCoreStatement(statementJSON).toString();
		byte[] bDataHash = HashUtility.hashForBlockchainByte(data);
		logger.info("Data hash: " + bDataHash.toString());
		BigInteger bPurposeCode = BigInteger.valueOf(purposeID);
		logger.info("Purpose Code: " + bPurposeCode);
		BigInteger bPurposeVersion = BigInteger.valueOf(purposeVersion);
		logger.info("Purpose Version: " + bPurposeVersion);
		
		try {
			verificationRegisrty.enterLogEntry(bUserID, bDataHash, bPurposeCode, bPurposeVersion).send();
		} catch (Exception e) {
			logger.severe("Could not execute EnterLogEntry of xAPIVerificationRegistry.");
			e.printStackTrace();
			return Response.serverError().entity("Blockchain issue.").build();
		}

		
		// Forward to LRS (through learning locker)
		Client lrsClient = ClientBuilder.newClient();
		WebTarget target = lrsClient.target("https://lrs.tech4comp.dbis.rwth-aachen.de/data/xAPI/statements");
		Invocation.Builder invocationBuilder = target.request();
		invocationBuilder.header(HttpHeaders.AUTHORIZATION, "Basic YzQ1NWJkYzhjNTQ3NTUxYzJmZTNhZmRiM2IxYjlmNTExNzM2OTlhMTpjOGM0OGIxYWFkYjY0MmMzMmQ3ODk4OWNlNjI4NGJlOGIxN2ZhYWQ0");
		invocationBuilder.header("X-Experience-API-Version", "1.0.3");
		Response postResponse = invocationBuilder.post(Entity.entity(statementJSON.toString(), MediaType.APPLICATION_JSON));
		logger.info("Done.");
		return Response.ok().build();
	}
	
	
	public boolean forwardStatement(String requestBody) {
		if (!serviceInitialised) {
			return false;
		}
		
		logger.info("PCS received Forward Statement request.");
		
		Agent mainAgent = Context.get().getMainAgent();
		ServiceAgent callerService = null;
		if (mainAgent instanceof ServiceAgent) {
			callerService = (ServiceAgent) mainAgent;
		}
		else {
			// Unknown what should happen if caller isn't a service
			return false;
		}
		String serviceID = callerService.getServiceNameVersion().getName();
		
		logger.info("Caller service ID is: " + serviceID);
		
		JSONTokener tokener = new JSONTokener(requestBody);
		JSONObject entryJSON = null;
		JSONObject statementJSON = null;
		int purposeID;
		String courseID;
		try {
			entryJSON = new JSONObject(tokener);
			statementJSON = entryJSON.getJSONObject("statement");
			purposeID = entryJSON.getInt("purpose");
			courseID = entryJSON.getString("course");
		} catch (JSONException e) {
			logger.warning("Could not parse JSON body of Forward Statement request.");
			return false;
		}
		
		String studentPseudonym = statUtil.getActorEmail(statementJSON);
		if (studentPseudonym == null) {
			logger.warning("Invalid xAPI Statement: Missing actor account details.");
			return false;
		}
		
		String studentID = database.SelectStudentWithPseudonym(studentPseudonym, serviceID, courseID);
		if (studentID == null) {
			logger.warning("Error in Forward Statement, possibly wrong pseudonym.");
			return false;
		}
		
		// Check user consent
		List<Integer> purposeIDs;
		List<Integer> purposeVersions;
		try {
			Tuple3<List<Integer>, List<Integer>, BigInteger> tuple = 
					getConsent(studentID, serviceID, courseID);
			purposeIDs = tuple.component1();
			purposeVersions = tuple.component2();
		} catch (Exception e) {
			logger.severe("Blockchain error in Forward Statement.");
			return false;
		}
		
		int purposeIndex = purposeIDs.indexOf(purposeID);
		if (purposeIndex < 0) {
			// The user did not give permission
			logger.warning("Data collector [" + serviceID + "] calling Forward Statement "
					+ "with data user has not consented to!");
			return false;
		}
		int purposeVersion = purposeVersions.get(purposeIndex);
		
		logger.info("Attempting to log entry in xAPIVerificationRegistry:");
		// Log to verification registry
		byte[] bUserID = HashUtility.hashForBlockchainByte(studentID);
		logger.info("Student ID and hash: " + studentID + " -> " + HashCode.fromBytes(bUserID));
		String data = statUtil.extractCoreStatement(statementJSON).toString();
		byte[] bDataHash = HashUtility.hashForBlockchainByte(data);
		logger.info("Data hash: " + bDataHash.toString());
		BigInteger bPurposeCode = BigInteger.valueOf(purposeID);
		logger.info("Purpose Code: " + bPurposeCode);
		BigInteger bPurposeVersion = BigInteger.valueOf(purposeVersion);
		logger.info("Purpose Version: " + bPurposeVersion);
		
		try {
			verificationRegisrty.enterLogEntry(bUserID, bDataHash, bPurposeCode, bPurposeVersion).send();
		} catch (Exception e) {
			logger.severe("Could not execute EnterLogEntry of xAPIVerificationRegistry.");
			e.printStackTrace();
			return false;
		}
		
		// Forward to LRS (through learning locker)
		// TODO: Make call to mobSOS instead of this
		// TODO: Each service+course should have their own store! <- NOT NECESSARY: each student+course+store has its own pseudonym
		Client lrsClient = ClientBuilder.newClient();
		WebTarget target = lrsClient.target("https://lrs.tech4comp.dbis.rwth-aachen.de/data/xAPI/statements");
		Invocation.Builder invocationBuilder = target.request();
		invocationBuilder.header(HttpHeaders.AUTHORIZATION, "Basic YzQ1NWJkYzhjNTQ3NTUxYzJmZTNhZmRiM2IxYjlmNTExNzM2OTlhMTpjOGM0OGIxYWFkYjY0MmMzMmQ3ODk4OWNlNjI4NGJlOGIxN2ZhYWQ0");
		invocationBuilder.header("X-Experience-API-Version", "1.0.3");
		Response postResponse = invocationBuilder.post(Entity.entity(statementJSON.toString(), MediaType.APPLICATION_JSON));
		logger.info("Done.");
		return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////
	///                               Outgoing HTTP                               ///
	/////////////////////////////////////////////////////////////////////////////////
	
	@GET
	@Path("/get-l2p-services")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getL2PServices() {
		List<String> result = getLas2PeerServices();
		JSONArray tmp = new JSONArray(result);
		return Response.ok(tmp.toString()).build();
	}	
	
	/**
	 * This method makes a REST call to the main las2peer instance (bootstrap) to
	 * get the names of all active services.
	 * 
	 * @return A List of Strings containing the names of the services (without their
	 * versions).
	 */
	public List<String> getLas2PeerServices() {
		List<String> retVal = new ArrayList<String>();
		logger.info("BEFORE");
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(LAS2PEER_GET_SERVICES_ENDPOINT);
		Invocation.Builder invBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invBuilder.header(HttpHeaders.AUTHORIZATION, "Basic YWxpY2U6cHdhbGljZQ==");
		Response response = invBuilder.get();
		String responceBody = response.readEntity(String.class);
		logger.info(responceBody);
		
		JSONTokener tokener = new JSONTokener(responceBody);
		try {
			JSONArray nodesInfoJSON = new JSONArray(tokener);
			for (Object node : nodesInfoJSON) {
				JSONObject nodeJSON = (JSONObject) node;
				JSONObject nodeInfoJSON = nodeJSON.getJSONObject("nodeInfo");
				JSONArray nodeServicesJSON = nodeInfoJSON.getJSONArray("services");
				for (Object service : nodeServicesJSON) {
					JSONObject serviceJSON = (JSONObject) service;
					String serviceName = serviceJSON.getString("service-name");
					retVal.add(serviceName);
				}
			}
		} catch (JSONException e) {
			logger.warning("Error while getting node info from las2peer for finding services.");
			return null;
		}
		
		return retVal;
	}
	
	private JSONArray retrieveStatementsFromLRS(String studentPseudonym) {
		JSONObject main = new JSONObject();
		JSONObject account = new JSONObject();
		account.put("name", studentPseudonym);
		account.put("homePage", LRS_ACTOR_ACCOUNT_HOMEPAGE);
		main.put("account", account);
		String queryParam = URLEncoder.encode(main.toString(), StandardCharsets.UTF_8);
		String url = LRS_STATEMENT_QUERY_ENDPOINT + "?agent=" + queryParam;
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(url);
		Invocation.Builder invBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invBuilder.header(HttpHeaders.AUTHORIZATION, LRS_CLIENT_AUTHORISATION);
		invBuilder.header("X-Experience-API-Version", "1.0.3");
		Response response = invBuilder.get();
		String responceBody = response.readEntity(String.class);
		
		// TODO: Retrieve "more"
		// TODO: Place this in learning locker service?
		
		JSONObject responceJSON = new JSONObject(responceBody);
		
		JSONArray retVal = responceJSON.getJSONArray("statements");
		
		return retVal;
	}
	
	private boolean deleteAllStudentPseudonymStatementsFromLRS(String studentPseudonym) {
		JSONObject requestBody = new JSONObject();
		JSONObject filterJSON = new JSONObject();
		filterJSON.put("statement.actor.account.name", studentPseudonym);
		requestBody.put("filter", filterJSON);
		
		JSONObject responceJSON = new JSONObject();
		try {
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(LRS_STATEMENT_DELETE_ENDPOINT);
			Invocation.Builder invBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			invBuilder.header(HttpHeaders.AUTHORIZATION, LRS_CLIENT_AUTHORISATION);
			Response response = invBuilder.post(Entity.entity(requestBody.toString(), MediaType.APPLICATION_JSON));
			String responceBody = response.readEntity(String.class);
			
			responceJSON = new JSONObject(responceBody);
		} catch (Exception e) {
			return false;
		}
		
		return !responceJSON.isEmpty();		
	}
	
}
