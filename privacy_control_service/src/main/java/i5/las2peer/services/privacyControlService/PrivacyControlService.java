package i5.las2peer.services.privacyControlService;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

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
import org.web3j.tuples.generated.Tuple3;

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
import i5.las2peer.services.privacyControlService.smartContracts.PrivacyConsentRegistry;
import i5.las2peer.services.privacyControlService.smartContracts.SolidityTest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
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
		logger.info("Initializing service...");
		
		ServiceAgentImpl agent = (ServiceAgentImpl) Context.getCurrent().getServiceAgent();
        registryClient = ((EthereumNode) agent.getRunningAtNode()).getRegistryClient();
        consentRegistry = registryClient.loadSmartContract(PrivacyConsentRegistry.class, "0xC58238a482e929584783d13A684f56Ca5249243E");
        
        if (consentRegistry == null) {
        	return Response.serverError().entity("Error getting contract.").build();
        }
		
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
