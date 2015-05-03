package com.saypenis.speech.recognition;

import java.io.InputStream;
import java.util.List;

import com.google.common.collect.Lists;

import edu.cmu.sphinx.result.WordResult;

public class NoopRecognitionService implements RecognitionService {

	@Override
	public List<WordResult> recognize(InputStream input) {
		return Lists.newArrayList();
	}

}
