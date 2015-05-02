package com.saypenis.speech.api.test;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.saypenis.speech.api.RecognizeUploadResource;

public class RecognizeUploadResourceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(RecognizeUploadResource.class);
	}
	
	@Test
	public void testRecognizeValidUpload() {
		
	}
	
}
