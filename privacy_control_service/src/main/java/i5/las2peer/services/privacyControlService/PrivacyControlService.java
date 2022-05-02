package i5.las2peer.services.privacyControlService;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.json.JSONObject;
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

	@GET
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

	/////////////////////////////////////////////////////////////////////////////////
	///                                  CRUD                                     ///
	/////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/register/manager")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerManager(Manager manager) {
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
		int result = database.InsertStudent(student);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();
		}

		String responseMessage = "Student " + student.getEmail() + " succesfully registered.";
		return Response.ok(responseMessage).build();
	}

	// PURPOSE IN COURSE
	
	//TODO: ADD MULTIPLE ADDITION AND DELETION
	@POST
	@Path("/register/purpose-in-course/{purposeid}/{serviceid}/{courseid}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response registerPurposeInCourse(
			@PathParam(value = "purposeid") int purposeID,
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {

		int result = database.InsertPurposeInCourse(purposeID, serviceID, courseID);
		if (result <= 0) {
			return Response.serverError().entity("Database error.").build();
		}
		
		String responseMessage = "Purpose with ID " + purposeID + " succesfully added to course with ID " + serviceID
				+ "|" + courseID;
		return Response.ok(responseMessage).build();
	}
	
	@GET
	@Path("/purpose-in-course/{serviceid}/{courseid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposesInCourse(
			@PathParam(value = "serviceid") String serviceID,
			@PathParam(value = "courseid") String courseID) {
		
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
	@Path("/consent/{studentid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentConsentOverview(@PathParam(value = "studentid") String studentID) {
		JSONArray result = database.SelectCoursesWithStudent(studentID);
		
		return Response.ok().entity(result.toString()).build();
	}

	@GET
	@Path("/get/{userid}/{serviceid}/{courseid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplate(@PathParam(value = "userid") String userID,
			@PathParam(value = "serviceid") String serviceID, @PathParam(value = "courseid") String courseID) {

		try {
			byte[] bUserID = Util.soliditySha3(userID);
			byte[] bServiceID = Util.soliditySha3(serviceID);
			byte[] bCourseID = Util.soliditySha3(courseID);

			logger.info("GET user: " + userID + " is " + bUserID.toString());
			logger.info("GET service:  " + serviceID + " is " + bServiceID.toString());
			logger.info("GET course: " + courseID + " is " + bCourseID.toString());

			Tuple3<List<BigInteger>, BigInteger, BigInteger> tmpTuple = consentRegistry
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
			retVal.put("privacyVersion", tmpTuple.component2().intValue());
			retVal.put("timestamp", tmpTuple.component3().intValue());

			return Response.ok().entity(retVal.toString()).build();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return Response.serverError().build();
		}

	}

	@GET
	@Path("/postConsent")
	@Produces(MediaType.TEXT_PLAIN)
	public Response postTemplate() {
		try {
			String userID = "Ring";
			String serviceID = "Beat";
			String internalID = "Drum";
			List<BigInteger> purposes = new ArrayList<BigInteger>();
			purposes.add(BigInteger.TWO);
			BigInteger privacyVersion = BigInteger.TEN;

			byte[] bUserID = Util.soliditySha3(userID);
			byte[] bServiceID = Util.soliditySha3(serviceID);
			byte[] bInternalID = Util.soliditySha3(internalID);

			logger.info("POST user: " + bUserID.toString());
			logger.info("POST service: " + bServiceID.toString());
			logger.info("POST internal: " + bInternalID.toString());

			consentRegistry.storeConsent(bUserID, bServiceID, bInternalID, purposes, privacyVersion).send();
			return Response.ok().entity("Completed").build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError().entity("Not completed").build();
		}
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/purpose-list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposeList() {
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
