package com.saypenis.speech.api.serialization;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.LogMath;

public final class SerializationUtils {

	private SerializationUtils() {}
	
	public static String serialize(List<WordResult> wordResults) {
		List<WordResultBean> beans = Lists.newArrayList();
    	for (WordResult wr : wordResults) {
    		beans.add(new WordResultBean(wr.getWord().toString(),  
    				LogMath.getLogMath().logToLinear((float)wr.getConfidence())));
    	}
    	
    	Gson gson = new Gson();
    	return gson.toJson(beans);
	}
	
	public static String transcribe(List<WordResult> wordResults) {
		StringBuilder builder = new StringBuilder();
		for (WordResult wordResult : wordResults) {
			builder.append(wordResult.getWord().getSpelling()).append(" ");
		}
		return builder.toString();
	}
	
}
