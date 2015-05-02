package com.saypenis.speech.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("status")
public class StatusResource {

	private static final String ENDPOINT_STATUS = "ENDPOINT_STATUS";
	
	public enum EndpointStatus {
		DOWN, SLOW, FAST
	}
	
	private static final Logger log = LoggerFactory.getLogger(StatusResource.class);
	
	public static EndpointStatus getStatus() {
		return EndpointStatus.valueOf(
				System.getProperty(ENDPOINT_STATUS, EndpointStatus.DOWN.name()));
	}
	
	@GET 
	@Produces("application/json")
	public String get() {
		EndpointStatus status = getStatus();
    	log.debug("Status requested. status={}", status);
    	Gson gson = new Gson();
		return gson.toJson(status);
	}
	
}
