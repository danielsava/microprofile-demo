package com.rafabene.microprofile;


import org.eclipse.microprofile.metrics.annotation.*;
import org.eclipse.microprofile.openapi.annotations.*;
import org.eclipse.microprofile.openapi.annotations.tags.*;
import org.eclipse.microprofile.opentracing.*;
import org.slf4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@ApplicationScoped
@Path("/api")
@Tag(name = "Recommendation Service", description = "Return the recommendation service")
public class RecommendationEndpoint {

	private static final String RESPONSE_STRING_FORMAT = "recommendation v1 from '%s': %d";

    private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String HOSTNAME = parseContainerIdFromHostname(
			System.getenv().getOrDefault("HOSTNAME", "unknown")
	);

	static String parseContainerIdFromHostname(final String hostname) {
		return hostname.replaceAll("recommendation-v\\d+-", "");
	}

    /**
     * Counter to help us see the lifecycle
     */
    private int count = 0;

	/**
	 * Flag for throwing a 503 when enabled
	 */
	private boolean misbehave = false;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Traced
    @Counted(name = "recommendationCount", description = "Number of time recommendation is requested")
	@Operation(description = "Get the Recommendation version and counter")
	public Response doGet() {
		logger.info(String.format("recommendation request from %s: %d", HOSTNAME, count));
		if (misbehave) {
			count = 0;
			logger.info(String.format("Misbehaving %d", count));
			return Response
					.status(Response.Status.SERVICE_UNAVAILABLE)
					.entity(String.format("recommendation misbehavior from '%s'\n", HOSTNAME))
					.build();
		}else{
			count++;
			logger.info(String.format("recommendation request from %s: %d", HOSTNAME, count));
			return Response
					.ok(String.format(RESPONSE_STRING_FORMAT, HOSTNAME, count))
					.build();
		}
	}


    @GET
    @Produces("text/plain")
    @Path("/misbehave")
    public Response misbehave(){
        this.misbehave = true;
        logger.info("'misbehave' has been set to 'true'");
        return Response.ok("Following requests to '/' will return a 503\n").build();
    }

    @GET
    @Produces("text/plain")
    @Path("/behave")
    public Response behave(){
        this.misbehave = false;
        logger.info("'misbehave' has been set to 'false'");
        return Response.ok("Following requests to '/' will return a 200\n").build();
    }
}
