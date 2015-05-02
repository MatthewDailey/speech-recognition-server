package com.saypenis.speech.recognition;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

/**
 * Wrapper for CMUSphinx recognition service.
 * 
 * Note that input stream must have sample rate 16000Hz and be single-channel.
 * 
 * @author mdailey
 *
 */
public class CmuSphinxRecognitionService implements RecognitionService {

	private final static Logger log = LoggerFactory.getLogger(CmuSphinxRecognitionUtils.class);
	
	private final PreAllocatingStreamSpeechRecognizer recognizer;
	
	public CmuSphinxRecognitionService(PreAllocatingStreamSpeechRecognizer recognizer) {
		this.recognizer = recognizer;
	}

	@Override
	public List<WordResult> recognize(InputStream input) {
		Stopwatch recognizeStopwatch = Stopwatch.createStarted();
		SpeechResult result = recognizer.recognize(input);    	
		log.debug("Finished recognition. Took {} ms.", recognizeStopwatch.elapsed(TimeUnit.MILLISECONDS));

		return result.getWords();
	}

}
