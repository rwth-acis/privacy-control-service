package i5.las2peer.services.privacyControlService;

import java.math.BigInteger;
import java.net.HttpURLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.web3j.protocol.core.RemoteFunctionCall;

import i5.las2peer.api.Context;
import i5.las2peer.api.security.UserAgent;
import i5.las2peer.classLoaders.Logger;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
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
	
	private SolidityTest contract;

	/**
	 * Template of a get function.
	 * 
	 * @return Returns an HTTP response with the username as string content.
	 */
	@GET
	@Path("/get")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTemplate() {
		String counter = contract.getCounter().toString();
//		UserAgent userAgent = (UserAgent) Context.getCurrent().getMainAgent();
//		String name = userAgent.getLoginName();
		return Response.ok().entity(counter).build();
	}

	/**
	 * Template of a post function.
	 * 
	 * @param myInput The post input the user will provide.
	 * @return Returns an HTTP response with plain text string content derived from the path input param.
	 */
	@POST
	@Path("/increment")
	public Response postTemplate() {
		contract.incCounter();
		logger.info("INCREMENTED");
		return Response.ok().entity("Incremented").build();
	}

	// TODO your own service methods, e. g. for RMI

}
