package i5.las2peer.services.privacyControlService;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.web3j.tuples.generated.Tuple3;

import i5.las2peer.api.Context;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.p2p.EthereumNode;
import i5.las2peer.registry.ReadWriteRegistryClient;
import i5.las2peer.registry.Util;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.security.ServiceAgentImpl;
import i5.las2peer.services.privacyControlService.smartContracts.DataProcessingPurposes;
import i5.las2peer.services.privacyControlService.smartContracts.PrivacyConsentRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import jnr.ffi.Struct.int16_t;
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
@ServicePath("/pieces")
public class PrivacyControlService extends RESTService {

	public final static L2pLogger logger = L2pLogger.getInstance(PrivacyControlService.class.getName());

	private static ReadWriteRegistryClient registryClient;
	private static PrivacyConsentRegistry consentRegistry;
	private static DataProcessingPurposes purposesRegistry;
	//private static XAPIVerificationRegistry verificationRegisrty;

	private static DBUtility database;

	private static boolean serviceInitialised = false;

	public PrivacyControlService() {
	}
	
	@POST
	@Path("/init")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(value = { @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK") })
	public Response init() {
		if (serviceInitialised) {
			return Response.ok("Service already initialised.").build();
		}

		L2pLogger.setGlobalConsoleLevel(Level.INFO);
		logger.info("Initializing service...");

		ServiceAgentImpl agent = (ServiceAgentImpl) Context.getCurrent().getServiceAgent();
		registryClient = ((EthereumNode) agent.getRunningAtNode()).getRegistryClient();
		// TODO: Put this in environment variables.
		consentRegistry = registryClient.loadSmartContract(PrivacyConsentRegistry.class,
				"0xC58238a482e929584783d13A684f56Ca5249243E");
		purposesRegistry = registryClient.loadSmartContract(DataProcessingPurposes.class,
				"0x2cCEc92848aA65A79dEa527B0449e4ce6f86472c");

		if (consentRegistry == null) {
			logger.severe("Could not load PrivacyConsentRegistry smart contract.");
			return Response.serverError().entity("Error getting contract: PrivacyConsentRegistry.").build();
		}
		if (purposesRegistry == null) {
			logger.severe("Could not load DataProcessingPurposes smart contract.");
			return Response.serverError().entity("Error getting contract: DataProcessingPurposes.").build();
		}
		// TODO: set correct registry
		if (consentRegistry == null) {
			logger.severe("Could not load PrivacyConsentRegistry smart contract.");
			return Response.serverError().entity("Error getting contract: PrivacyConsentRegistry.").build();
		}

		// TODO: Put parameters into environment variables
		database = new DBUtility();
		boolean dbflag = database.establishConnection("localhost", 1433, "PrivacyServiceDB", "SA", "privacyIS#1important!");
		if (!dbflag) {
			PrivacyControlService.logger.severe("Could not connect to PrivacyControlService database.");
			return Response.serverError().entity("Error while connecting to service's database").build();
		}
		dbflag = database.prepareStatements();
		if (!dbflag) {
			PrivacyControlService.logger.severe("Could not connect prepare database statements.");
			return Response.serverError().entity("Database prepared statement error.").build();

		}

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

	/////////////////////////////////////////////////////////////////////////////////
	///                                  CRUD                                     ///
	/////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/register/manager")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerManager(Manager manager) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		int result = database.InsertManager(manager);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();

		}

		String responseMessage = "Manager " + manager.getName() + " succesfully registered.";
		return Response.ok(responseMessage).build();
	}

	@POST
	@Path("/register/service")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerService(Service service) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	public Response getService(@PathParam(value = "serviceID") String serviceID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		JSONObject retVal = database.SelectServiceWithCourses(serviceID);
		if (retVal == null) {
			return Response.serverError().entity("Database error.").build();
		}
		if (retVal.isEmpty()) {
			return Response.status(404).entity("Service not found.").build();
		}

		return Response.ok().entity(retVal.toString()).build();
	}

	@GET
	@Path("/manager/{managerid}/services")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getManagerServices(@PathParam(value = "managerid") String managerID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	public Response registerCourse(Course course) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	public Response getCourse(@PathParam(value = "serviceID") String serviceID,
			@PathParam(value = "courseID") String courseID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	public Response editCourse(Course course) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	public Response registerStudent(Student student) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	public Response registerPurposesInCourse(InputStream input) {
		
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		JSONTokener tokener = new JSONTokener(input);
		JSONObject picJSON = null;
		try {
			picJSON = new JSONObject(tokener);
		} catch (JSONException e) {
			logger.warning("Could not parse JSON in POST purpose in course request.");
			return Response.status(400).entity("Invalid JSON.").build();
		}
		
		logger.info(picJSON.toString());
		String courseID;
		String serviceID;
		List<Integer> purposeIDs = new ArrayList<Integer>();
		try {
			courseID = picJSON.getString("courseID");
			logger.info(courseID);
			serviceID = picJSON.getString("serviceID");
			logger.info(serviceID);
			for (Object x : picJSON.getJSONArray("purposes").toList()) {
				purposeIDs.add((int) x);
				logger.info("aa");
			}
		} catch (JSONException e) {
			logger.warning("Mandatory fields not found in request JSON.");
			e.printStackTrace();
			return Response.status(400).entity("Invalid JSON.").build();
		} catch (ClassCastException e) {
			logger.warning("Purpose list contains non-integer ids.");
			return Response.status(400).entity("Invalid JSON.").build();
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
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {
		
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {
		
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID,
			Student student) {
		
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	
	
	/////////////////////////////////////////////////////////////////////////////////
	///                                Consent                                    ///
	/////////////////////////////////////////////////////////////////////////////////
	
	@GET
	@Path("/consent/{studentid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentConsentOverview(@PathParam(value = "studentid") String studentID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		JSONArray result = database.SelectCoursesWithStudent(studentID);
		
		return Response.ok().entity(result.toString()).build();
	}

	@GET
	@Path("/consent/{userid}/{serviceid}/{courseid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplate(@PathParam(value = "userid") String userID,
			@PathParam(value = "serviceid") String serviceID, @PathParam(value = "courseid") String courseID) {

		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
		}
		
		try {
			byte[] bUserID = Util.soliditySha3(userID);
			byte[] bServiceID = Util.soliditySha3(serviceID);
			byte[] bCourseID = Util.soliditySha3(courseID);

			logger.info("GET user: " + userID + " is " + bUserID.toString());
			logger.info("GET service:  " + serviceID + " is " + bServiceID.toString());
			logger.info("GET course: " + courseID + " is " + bCourseID.toString());

			Tuple3<List<BigInteger>, List<BigInteger>, BigInteger> tmpTuple = consentRegistry
					.getConsentInfo(bUserID, bServiceID, bCourseID).send();
			logger.info("Tupple info: " + tmpTuple.component1().toString() + " " + tmpTuple.component2() + " "
					+ tmpTuple.component3());

			JSONObject retVal = new JSONObject();
			retVal.put("userID", userID);
			retVal.put("serviceID", serviceID);
			retVal.put("courseID", courseID);
			JSONArray purposesJSON = new JSONArray();
			for (BigInteger bi : tmpTuple.component1()) {
				purposesJSON.put(bi.intValue());
			}
			retVal.put("purposes", purposesJSON);
			JSONArray purposeVersionsJSON = new JSONArray();
			for (BigInteger bi : tmpTuple.component2()) {
				purposeVersionsJSON.put(bi.intValue());
			}
			retVal.put("privacyVersion", purposeVersionsJSON);
			retVal.put("timestamp", tmpTuple.component3().intValue());

			return Response.ok().entity(retVal.toString()).build();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return Response.serverError().build();
		}

	}
	
	@POST
	@Path("/consent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postStudentConsent(InputStream input) {
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
		
		List<BigInteger> purposes = new ArrayList<BigInteger>();
		for (int i = 0; i < purposesJSON.length(); i++) {
			int purpose = purposesJSON.getInt(i);
			purposes.add(BigInteger.valueOf(purpose));
		}
		
		// TODO: Check in DB if student attends course, etc.
		
		byte[] bStudentID = Util.soliditySha3(studentID);
		byte[] bCourseID = Util.soliditySha3(courseID);
		byte[] bServiceID = Util.soliditySha3(serviceID);
		
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
	///                                Purpose                                    ///
	/////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/purpose-list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposeList() {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	
	public Purpose getPurposeBlockchain(int purposeID) {
		
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

	@GET
	@Path("/purpose-list/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposeItem(@PathParam(value = "id") int purposeID) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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
	public Response postPurpose(Purpose purpose) {
		if (!serviceInitialised) {
			return Response.status(500).entity("Service not initialised").build();
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

	@GET
	@Path("/pseudonym/{input}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPseudonym(@PathParam(value = "input") String input) {
		Random random = new Random();
		String hash = HashUtility.hash(input, String.valueOf(random.nextInt()));
		return Response.ok(hash).build();
	}
}
