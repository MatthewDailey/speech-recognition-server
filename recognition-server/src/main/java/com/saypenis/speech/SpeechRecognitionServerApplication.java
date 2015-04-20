package com.saypenis.speech;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class SpeechRecognitionServerApplication extends ResourceConfig {

	public SpeechRecognitionServerApplication() {
		packages("com.saypenis.speech");
		register(MultiPartFeature.class); 
	}

}
