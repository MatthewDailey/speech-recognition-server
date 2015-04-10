package com.saypenis.speech.recognition;

import java.io.InputStream;
import java.util.List;

import edu.cmu.sphinx.result.WordResult;

public interface RecognitionService {
	List<WordResult> recognize(InputStream input);
}
