package com.saypenis.speech.api;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saypenis.speech.api.serialization.RoundBean;
import com.saypenis.speech.aws.AwsSupplier;
import com.saypenis.speech.aws.SayPenisAwsUtils;
import com.saypenis.speech.aws.SayPenisConfiguration;

public class ReadTopResourceTest extends JerseyTest {
	
	private final RoundBean testBean1 = new RoundBean("cache_round_1", 1, 1, 1, "cache-1",
			101, "s3://uri", "cache_test_user", "cache_test");

	@Override
	protected Application configure() {
		return new ResourceConfig(ReadTopResource.class);
	}
	
	@Before
	public void before() {
		storeRound(testBean1);
	}
	
	@Test
	public void testReadPage() {
		String jsonResult = target("read/top").queryParam("page", "0").queryParam("page_size", "1")
				.request().get(String.class);
		
		List<RoundBean> roundBeans = deserialize(jsonResult);
		assertEquals(1, roundBeans.size());
	}
	
	@Test
	public void testCacheRefresh() throws InterruptedException {
		String jsonResult = target("read/top").queryParam("page", "0").queryParam("page_size", "2")
				.request().get(String.class);
		
		List<RoundBean> roundBeans = deserialize(jsonResult);
		assertEquals(2, roundBeans.size());
		RoundBean currentTopRound = roundBeans.get(0);
		
		RoundBean betterRound = getBetterRound(currentTopRound);
		storeRound(getBetterRound(currentTopRound));
		Thread.sleep(SayPenisConfiguration.readCacheRefreshMillis() * 2);
		
		String jsonResultAfterWrite = target("read/top").queryParam("page", "0").queryParam("page_size", "2")
				.request().get(String.class);
		
		List<RoundBean> roundBeansAfterWrite = deserialize(jsonResultAfterWrite);
		assertEquals(2, roundBeansAfterWrite.size());
		assertEquals(betterRound, roundBeansAfterWrite.get(0));
		assertEquals(currentTopRound, roundBeansAfterWrite.get(1));
	}
	
	private List<RoundBean> deserialize(String json) {
		Type type = new TypeToken<ArrayList<RoundBean>>() {}.getType();
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}
	
	private void storeRound(RoundBean roundBean) {
		SayPenisAwsUtils.storeToDynamoAsync(SayPenisConfiguration.roundTable(), roundBean, 
				AwsSupplier.getDynamo());
	}

	private RoundBean getBetterRound (RoundBean roundBean) {
		double score = roundBean.score + 1d;
		return new RoundBean("cache_round_" + score, 1, 1, 1, "cache-" + score, 
				score, "s3://uri", "cache_test_user", "cache_test");
	}
	
}
