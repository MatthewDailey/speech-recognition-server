package com.saypenis.speech.recognition;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.collect.Lists;
import com.saypenis.speech.perf.PerfUtils;
import com.saypenis.speech.perf.PerfUtils.LoggingTimer;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;

public class PreAllocatingStreamSpeechRecognizer extends AbstractSpeechRecognizer {

	public PreAllocatingStreamSpeechRecognizer(Configuration configuration)
        throws IOException {
        super(configuration);
        
        LoggingTimer allocateTimer = PerfUtils.getTimerStarted("recoginizer.allocate()");
        recognizer.allocate();
        allocateTimer.log();
    }
	
	public List<WordResult> recognize(InputStream stream) {
		startRecognition(stream);
		SpeechResult result;
		List<WordResult> wordResults = Lists.newLinkedList();
		while ((result = getResult()) != null) {
			wordResults.addAll(result.getWords());
		}
		stopRecognition();
		return wordResults;
	}

    public void startRecognition(InputStream stream) {
    	LoggingTimer recognitionTimer = PerfUtils.getTimerStarted("PreallocatingRecognizer.startRecognition");
        startRecognition(stream, TimeFrame.INFINITE);
        recognitionTimer.log();
    }

    private void startRecognition(InputStream stream, TimeFrame timeFrame) {
        context.setSpeechSource(stream, timeFrame);
    }
    
    @Override
    public SpeechResult getResult() {
    	LoggingTimer getResultTimer = PerfUtils.getTimerStarted("PreallocatingRecognizer.getResult");
    	SpeechResult result = super.getResult();
    	getResultTimer.log();
    	return result;
    }

    public void stopRecognition() {
    	LoggingTimer deallocateTimer = PerfUtils.getTimerStarted("PreallocatingRecognizer.stopRecognition");
        recognizer.deallocate();
        deallocateTimer.log();
    }
}
