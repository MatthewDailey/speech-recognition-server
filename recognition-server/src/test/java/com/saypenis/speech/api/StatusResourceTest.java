package com.saypenis.speech.api;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.saypenis.speech.api.StatusResource;

public class StatusResourceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(StatusResource.class);
	}
	
	@Test
	public void testStatusDefault() {
		String status = target("status").request().get(String.class);
		assertEquals("OFFLINE", status);
	}
	
	@Test
	public void testStatusSetOnline() {
		String expectStatus = "ONLINE";
		System.setProperty(StatusResource.ENDPOINT_STATUS, expectStatus);
		String status = target("status").request().get(String.class);
		assertEquals(expectStatus, status);
		System.clearProperty(StatusResource.ENDPOINT_STATUS);
	}
	
	@Test
	public void testStatusSetLowAvailability() {
		String expectStatus = "LOW_AVAILABILITY";
		System.setProperty(StatusResource.ENDPOINT_STATUS, expectStatus);
		String status = target("status").request().get(String.class);
		assertEquals(expectStatus, status);
		System.clearProperty(StatusResource.ENDPOINT_STATUS);
	}
}
