package co.speechre.api;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("timeout")
public class TimeoutTest {

	private final static Logger log = LoggerFactory.getLogger(TimeoutTest.class);
	
	private final static int TIMEOUT = 10000;
	
	@GET 
	@Produces("application/json")
	public String get() throws IOException, InterruptedException {
		log.debug("Received request to timeout test. Sleeping for {} ms.", TIMEOUT);
		Thread.sleep(TIMEOUT);
    	log.debug("Completed request!");
		return "Timeout complete.";
	}

}
