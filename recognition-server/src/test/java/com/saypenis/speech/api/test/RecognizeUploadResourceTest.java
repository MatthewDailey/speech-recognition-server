package com.saypenis.speech.api.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Test;

import com.google.gson.Gson;
import com.saypenis.speech.SayPenisConstants;
import com.saypenis.speech.api.RecognizeUploadResource;
import com.saypenis.speech.api.StatusResource;
import com.saypenis.speech.api.serialization.ErrorResultBean;
import com.saypenis.speech.api.serialization.SuccessResultBean;

public class RecognizeUploadResourceTest extends JerseyTest {

	private final static long testLat = 10;
	private final static long testLon = 45;
	private final static String testUserId = "integration_test_user_id";
	private final static String testName = "integration_test_name";
	
	@Override
	protected Application configure() {
		 ResourceConfig resourceConfig = new ResourceConfig(RecognizeUploadResource.class);
		 resourceConfig.register(MultiPartFeature.class); 
		 return resourceConfig;
	}
	
	@Override
	protected void configureClient(ClientConfig config) {
		config.register(MultiPartFeature.class);
	}
	
	@After
	public void after() {
		System.clearProperty(StatusResource.ENDPOINT_STATUS);		
	}
	
	@Test
	public void testRecognizeValidUploadButServiceDown() throws Throwable {
		Gson gson = new Gson();
		ErrorResultBean resultBean = gson.fromJson(
				makeApiCall("src/test/java/com/saypenis/speech/api/test/resources/clean_test_penis.wav"), 
				ErrorResultBean.class);
		assertEquals(SayPenisConstants.ERROR_SERVICE_OFFLINE, resultBean.resultCode);
	}
	
	@Test
	public void testRecognizeValidUpload() throws Throwable {
		System.setProperty(StatusResource.ENDPOINT_STATUS, "ONLINE");
		
		Gson gson = new Gson();
		SuccessResultBean resultBean = gson.fromJson(makeApiCall("src/test/java/com/saypenis/speech/api/test/resources/clean_test_penis.wav"), 
				SuccessResultBean.class);
		assertTrue(resultBean.success);
		assertEquals(testLat, resultBean.lat);
		assertEquals(testLon, resultBean.lon);
		assertEquals(testName, resultBean.name);
		assertEquals(testUserId, resultBean.user_id);
	}
	
	@Test
	public void testRecognizeTooManyPenisesUpload() throws Throwable {
		System.setProperty(StatusResource.ENDPOINT_STATUS, "ONLINE");
		
		Gson gson = new Gson();
		ErrorResultBean resultBean = gson.fromJson(
				makeApiCall("src/test/java/com/saypenis/speech/api/test/resources/clean_test_penis_penis.wav"), 
				ErrorResultBean.class);
		assertFalse(resultBean.success);
		assertEquals(SayPenisConstants.ERROR_TOO_MANY_PENISES, resultBean.resultCode);
	}
	
	@Test
	public void testRecognizeNonPenisesUpload() throws Throwable {
		System.setProperty(StatusResource.ENDPOINT_STATUS, "ONLINE");
		
		Gson gson = new Gson();
		ErrorResultBean resultBean = gson.fromJson(
				makeApiCall("src/test/java/com/saypenis/speech/api/test/resources/clean_test_car_penis_what.wav"), 
				ErrorResultBean.class);
		assertFalse(resultBean.success);
		assertEquals(SayPenisConstants.ERROR_NON_PENIS_WORDS, resultBean.resultCode);
	}
	
	private String makeApiCall(String pathToUploadFile) throws Throwable {
		FormDataMultiPart form = new FormDataMultiPart();
		form.field("lat", String.valueOf(testLat));
		form.field("lon", String.valueOf(testLon));
		form.field("user_id", testUserId);
		form.field("name", testName);
		
		FormDataBodyPart formDataBodyPart = new FormDataBodyPart("file", 
				new FileInputStream(new File(pathToUploadFile)),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		form.bodyPart(formDataBodyPart);
		
		try {
			Future<String> result = target("recognize/upload").request(MediaType.MULTIPART_FORM_DATA_TYPE).async()
				.post(Entity.entity(form, form.getMediaType()), String.class);
			return result.get(10, TimeUnit.SECONDS);
		} finally {
			form.close();
		}
	}
	
}
