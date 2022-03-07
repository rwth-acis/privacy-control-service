package i5.las2peer.services.privacyControlService;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;

import i5.las2peer.api.Context;
import i5.las2peer.api.security.UserAgent;
import i5.las2peer.classLoaders.Logger;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.p2p.EthereumNode;
import i5.las2peer.registry.ReadWriteRegistryClient;
import i5.las2peer.registry.Util;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.security.ServiceAgentImpl;
import i5.las2peer.services.privacyControlService.smartContracts.DataProcessingPurposes;
import i5.las2peer.services.privacyControlService.smartContracts.PrivacyConsentRegistry;
import i5.las2peer.services.privacyControlService.smartContracts.SolidityTest;
import i5.las2peer.services.privacyControlService.smartContracts.XAPIVerificationRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import modelPOJOs.Purpose;
import rice.p2p.util.DebugCommandHandler;


// TODO Describe your own service
/**
 *	The Privacy Control Service serves as a safeguard and checkpoint for user data being transmitted in the tech4comp project.
 *	It's two main features are consent handling and pseudonymisation.
 * 
 */
// TODO Adjust the following configuration
@Api
@SwaggerDefinition(
		info = @Info(
				title = "Privacy Control Service",
				version = "0.1.0",
				description = "The Privacy Control Service serves as a safeguard and checkpoint for "
						+ "user data being transmitted in the tech4comp project.",
				//termsOfService = "http://your-terms-of-service-url.com",
				contact = @Contact(
						name = "Boris Jovanovic",
						email = "jovanovic.boris@rwth-aachen.de"),
				license = @License(
						name = "your software license name",
						url = "http://your-software-license-url.com")))
@ServicePath("/pieces")
public class PrivacyControlService extends RESTService {
	
	private final static L2pLogger logger = L2pLogger.getInstance(PrivacyControlService.class.getName());
	
	private static ReadWriteRegistryClient registryClient;
	private static PrivacyConsentRegistry consentRegistry;
	private static DataProcessingPurposes purposesRegistry;
	private static XAPIVerificationRegistry verificationRegisrty;
	
	
	public PrivacyControlService() {
	}
	
	
	@GET
	@Path("/init")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "OK") })
	public Response init() {
		L2pLogger.setGlobalConsoleLevel(Level.INFO);
		logger.info("Initializing service...");
		
		ServiceAgentImpl agent = (ServiceAgentImpl) Context.getCurrent().getServiceAgent();
        registryClient = ((EthereumNode) agent.getRunningAtNode()).getRegistryClient();
        consentRegistry = registryClient.loadSmartContract(PrivacyConsentRegistry.class, "0xC58238a482e929584783d13A684f56Ca5249243E");
        purposesRegistry = registryClient.loadSmartContract(DataProcessingPurposes.class, "0x2cCEc92848aA65A79dEa527B0449e4ce6f86472c");
        
        if (consentRegistry == null) {
        	logger.severe("Could not load PrivacyConsentRegistry smart contract.");
        	return Response.serverError().entity("Error getting contract: PrivacyConsentRegistry.").build();
        }
        if (purposesRegistry == null) {
        	logger.severe("Could not load DataProcessingPurposes smart contract.");
        	return Response.serverError().entity("Error getting contract: DataProcessingPurposes.").build();
        }
        //TODO: set correct registry
        if (consentRegistry == null) {
        	logger.severe("Could not load PrivacyConsentRegistry smart contract.");
        	return Response.serverError().entity("Error getting contract: PrivacyConsentRegistry.").build();
        }
		
        logger.info("Done.");
        return Response.ok(200).entity("Initialisation was successful.").build();
	}
	
	
	@GET
	@Path("/get/{userid}/{serviceid}/{internalid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplate(@PathParam(value = "userid") String userID,
			@PathParam(value = "serviceid") String serviceID, @PathParam(value = "internalid") String internalID) {
		
		try {
			byte[] bUserID = Util.soliditySha3(userID);
			byte[] bServiceID = Util.soliditySha3(serviceID);
			byte[] bInternalID = Util.soliditySha3(internalID);
			
			logger.info("GET user: " + userID + " is " + bUserID.toString());
			logger.info("GET service:  " + serviceID + " is " + bServiceID.toString());
			logger.info("GET internal: " + internalID + " is " + bInternalID.toString());
			
			Tuple3<List<BigInteger>, BigInteger, BigInteger> tmpTuple =  consentRegistry.getConsentInfo(bUserID, bServiceID, bInternalID).send();
			logger.info("Tupple info: " + tmpTuple.component1().toString() + " " + tmpTuple.component2() + " " + tmpTuple.component3());
			
			JSONObject retVal = new JSONObject();
			retVal.put("userID", userID);
			retVal.put("serviceID", serviceID);
			retVal.put("internalID", internalID);
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
	
	@GET
	@Path("/PurposeList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposeList() {
		logger.info("Attempting to retrieve purpose list...");
		Tuple4<List<BigInteger>, List<String>, List<String>, List<BigInteger>> tuple;
		List<BigInteger> ids = null;
		List<String> titles = null;
		List<String> descriptions = null;
		List<BigInteger> versions = null; 
		try {
			tuple = purposesRegistry.getAllPurposes().send();
			ids = tuple.component1();
			titles = tuple.component2();
			descriptions = tuple.component3();
			versions = tuple.component4();
		} catch(Exception e) {
			logger.severe("Could not retrieve purpose list from chain.");
			e.printStackTrace();
			return Response.serverError().build();
		}
		
		logger.info("Purpose list retrieved successfully. Creating JSON as return value...");
		JSONArray retVal = new JSONArray();
		if (ids == null || titles == null || descriptions == null || versions == null) {
			logger.info("List is empy. Returning empty JSONArray.");
			return Response.ok(retVal.toString()).build();
		}
		try {
			logger.info("There are " + titles.size() + " items on the purpose list.");
			for (int i = 0; i < ids.size(); i++) {
				JSONObject tmp = new JSONObject();
				// TODO: For some ungodly reason, for the first purpose, the title and description
				// string get jumbled up, so this fix is necessary. At least until someone
				// can figure out why this happens.
				if (i == 0) {
					Tuple3<String, String, BigInteger> tuple2 = purposesRegistry.getPurpose(ids.get(i)).send();
					tmp.put("id", ids.get(i).intValue());
					tmp.put("title", tuple2.component1());
					tmp.put("description", tuple2.component2());
					tmp.put("version", tuple2.component3());
					retVal.put(tmp);
					continue;					
				} else {
					tmp.put("id", ids.get(i).intValue());
					tmp.put("title", titles.get(i));
					tmp.put("description", descriptions.get(i));
					tmp.put("version", versions.get(i));
					retVal.put(tmp);
				}
			}
		} catch(Exception e) {
			logger.severe("Error while processing purpose list data into JSON.");
			e.printStackTrace();
			return Response.serverError().build();
		}
		
		logger.info("Done.");
		return Response.ok(retVal.toString()).build();
	}
	
	
	@GET
	@Path("/PurposeList/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurposeItem(@PathParam(value = "id") int purposeID) {
		logger.info("Attempting to get purpose with ID: " + purposeID);
		try {
			Tuple3<String, String, BigInteger> tuple = purposesRegistry.getPurpose(BigInteger.valueOf(purposeID)).send();
			Purpose purpose = new Purpose(
					purposeID,
					tuple.component1(),
					tuple.component2(),
					tuple.component3().intValue()
					);
			logger.info("Got Purpose: " + purpose);
			return Response.ok(purpose.toJSON().toString()).build();
		} catch (Exception e) {
			logger.severe("Error while trying to get purpose list item.");
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	
	@POST
	@Path("/PurposeList")
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
		
		logger.info("Done.");
		return Response.ok().build();
	}
	
	
//	
//	/**
//	 * Template of a get function.
//	 * 
//	 * @return Returns an HTTP response with the username as string content.
//	 */
//	@GET
//	@Path("/get")
//	@Produces(MediaType.TEXT_PLAIN)
//	public Response getTemplate() {
//		if (testContract == null) {
//        	logger.info("IT'S NULL");
//		}
//		String counter = "";
//		try {
//			counter = testContract.getCounter().send().toString();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		UserAgent userAgent = (UserAgent) Context.getCurrent().getMainAgent();
////		String name = userAgent.getLoginName();
//		return Response.ok().entity(counter).build();
//	}
//
//	/**
//	 * Template of a post function.
//	 * 
//	 * @param myInput The post input the user will provide.
//	 * @return Returns an HTTP response with plain text string content derived from the path input param.
//	 */
//	@POST
//	@Path("/increment")
//	public Response postTemplate() {
//		try {
//			testContract.incCounter().send();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		logger.info("INCREMENTED");
//		return Response.ok().entity("Incremented").build();
//	}
//
//	// TODO your own service methods, e. g. for RMI

}
