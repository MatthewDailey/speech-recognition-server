package com.saypenis.speech.api.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Test;

import com.saypenis.speech.api.StatusResource;

public class StatusResourceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(StatusResource.class);
	}
	
	@After
	public void after() {
		System.clearProperty(StatusResource.ENDPOINT_STATUS);		
	}
	
	@Test
	public void testStatusDefault() {
		String status = target("status").request().get(String.class);
		assertEquals("READ_ONLY", status);
	}
	
	@Test
	public void testStatusSetOnline() {
		String expectStatus = "READ_WRITE";
		System.setProperty(StatusResource.ENDPOINT_STATUS, expectStatus);
		String status = target("status").request().get(String.class);
		assertEquals(expectStatus, status);
	}
	
}
