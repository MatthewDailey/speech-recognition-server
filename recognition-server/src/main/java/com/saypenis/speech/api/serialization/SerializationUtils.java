package com.saypenis.speech.api.serialization;

import java.util.List;

import com.google.common.collect.Lists;

import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.LogMath;

public final class SerializationUtils {

	private SerializationUtils() {}
	
	public static List<WordResultBean> serialize(List<WordResult> wordResults) {
		List<WordResultBean> beans = Lists.newArrayList();
    	for (WordResult wr : wordResults) {
    		beans.add(new WordResultBean(wr.getWord().toString(),  
    				LogMath.getLogMath().logToLinear((float)wr.getConfidence())));
    	}
    	return beans;
	}
	
}
