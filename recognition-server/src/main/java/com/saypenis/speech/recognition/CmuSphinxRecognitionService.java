package com.saypenis.speech.recognition;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class CmuSphinxRecognitionService implements RecognitionService {

	private final static Logger log = LoggerFactory.getLogger(CmuSphinxRecognitionUtils.class);

	@Override
	public List<WordResult> recognize(InputStream input) {
		try {
			Stopwatch createRecognizerStopwatch = Stopwatch.createStarted();
			PreAllocatingStreamSpeechRecognizer recognizer = new PreAllocatingStreamSpeechRecognizer(CmuSphinxRecognitionUtils.getConfiguration());
			log.debug("Finished creating recognized. It took {} ms", createRecognizerStopwatch.elapsed(TimeUnit.MILLISECONDS));

			Stopwatch recognizeStopwatch = Stopwatch.createStarted();
			recognizer.startRecognition(input);
			SpeechResult result = recognizer.getResult();    	
			recognizer.stopRecognition();
			log.debug("Finished recognition. Took {} ms.", recognizeStopwatch.elapsed(TimeUnit.MILLISECONDS));

			return result.getWords();
		} catch (IOException e) {
			log.error("Caught exception {} while trying to recognize.", e);
			return ImmutableList.of();
		}
	}

}
