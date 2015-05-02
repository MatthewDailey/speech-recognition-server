package com.saypenis.speech.recognition;

import edu.cmu.sphinx.api.Configuration;

public final class CmuSphinxRecognitionUtils {

	private CmuSphinxRecognitionUtils() {}
	
	public static Configuration getConfiguration() {
    	Configuration configuration = new Configuration();
    	configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
    	configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
    	configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");
    	return configuration;
	}
	
	public static Configuration getAdaptedConfiguration() {
    	Configuration configuration = new Configuration();
    	configuration.setAcousticModelPath("resource:/com/saypenis/speech/resources/en-us");
    	configuration.setDictionaryPath("resource:/com/saypenis/speech/resources/cmudict-en-us.dict");
    	configuration.setLanguageModelPath("resource:/com/saypenis/speech/resources/en-us.lm.dmp");
    	return configuration;
	}
}
