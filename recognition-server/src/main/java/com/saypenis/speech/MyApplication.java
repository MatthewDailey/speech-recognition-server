package com.saypenis.speech;

import org.glassfish.jersey.server.ResourceConfig;

import co.speechre.aws.AWS;

/**
 * Register singletons with resource config.
 * 
 * See http://stackoverflow.com/questions/18914130/when-to-use-singleton-annotation-of-jersey
 * 
 * @author mdailey
 */
public class MyApplication extends ResourceConfig {

	public MyApplication() {
		register(AWS.class);
	}
}
