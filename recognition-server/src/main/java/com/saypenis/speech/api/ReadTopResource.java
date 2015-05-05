package com.saypenis.speech.api;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.google.gson.Gson;
import com.saypenis.speech.api.read.PagingCaches;
import com.saypenis.speech.api.serialization.RoundBean;
import com.saypenis.speech.aws.SayPenisConfiguration;
import com.saypenis.speech.perf.PerfUtils;
import com.saypenis.speech.perf.PerfUtils.LoggingTimer;

@Path("/read/top")
public class ReadTopResource {
	
	@GET 
	@Produces("application/json")
	public String get(@DefaultValue("0") 
					@QueryParam("page") int page,
					@DefaultValue(SayPenisConfiguration.READ_PAGE_SIZE_DEFAULT) 
					@QueryParam("page_size") int pageSize) {
		LoggingTimer resourceTimer = PerfUtils.getTimerStarted("/read/top page=" + page + 
				" page_size=" + pageSize);
		
		RoundBean[] roundPage = PagingCaches.topRounds.getPageLoader().getPage(page, pageSize);
		
		resourceTimer.log();
		Gson gson = new Gson();
		return gson.toJson(roundPage);
	}
}
