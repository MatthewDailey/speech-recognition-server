package com.saypenis.speech.api;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saypenis.speech.aws.SayPenisConfiguration;
import com.saypenis.speech.perf.PerfUtils;
import com.saypenis.speech.perf.PerfUtils.LoggingTimer;

@Path("read/top")
public class ReadTopResource {
	private final static Logger log = LoggerFactory.getLogger(ReadTopResource.class);
	
	@GET 
	@Produces("application/json")
	public void get(@DefaultValue("0") 
					@QueryParam("page") int page,
					@DefaultValue(SayPenisConfiguration.READ_PAGE_SIZE_DEFAULT) 
					@QueryParam("page_size") int page_size) {
		LoggingTimer resourceTimer = PerfUtils.getTimerStarted("/read/top page=" + page);
		
		// TODO (mdailey): Have cache.
		
		// Read top page size from dynamo.
		// multiple paging strategies.
		
		resourceTimer.log();
	}
}
