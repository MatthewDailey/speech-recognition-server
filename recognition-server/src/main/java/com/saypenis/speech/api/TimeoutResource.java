package com.saypenis.speech.api;

import java.io.IOException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("timeout")
public class TimeoutResource {

	private final static Logger log = LoggerFactory.getLogger(TimeoutResource.class);
	
	@GET 
	@Produces("application/json")
	public String get(@DefaultValue("10000") @QueryParam("timeout") int timeout) throws IOException, InterruptedException {
		log.debug("Received request to timeout test. Sleeping for {} ms.", timeout);
		Thread.sleep(timeout);
    	log.debug("Completed request!");
		return "Timeout complete.";
	}

}