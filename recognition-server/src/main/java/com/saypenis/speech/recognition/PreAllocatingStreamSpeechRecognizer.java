package com.saypenis.speech.recognition;

import java.io.IOException;
import java.io.InputStream;

import com.saypenis.speech.perf.PerfUtils;
import com.saypenis.speech.perf.PerfUtils.LoggingTimer;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.util.TimeFrame;

public class PreAllocatingStreamSpeechRecognizer extends AbstractSpeechRecognizer {

	public PreAllocatingStreamSpeechRecognizer(Configuration configuration)
        throws IOException {
        super(configuration);
        
        LoggingTimer allocateTimer = PerfUtils.getTimerStarted("recoginizer.allocate()");
        recognizer.allocate();
        allocateTimer.log();
    }

    public void startRecognition(InputStream stream) {
    	LoggingTimer recognitionTimer = PerfUtils.getTimerStarted("PreallocatingRecognizer.startRecognition");
        startRecognition(stream, TimeFrame.INFINITE);
        recognitionTimer.log();
    }

    private void startRecognition(InputStream stream, TimeFrame timeFrame) {
        context.setSpeechSource(stream, timeFrame);
    }

    public void stopRecognition() {
    	LoggingTimer deallocateTimer = PerfUtils.getTimerStarted("PreallocatingRecognizer.stopRecognition");
        recognizer.deallocate();
        deallocateTimer.log();
    }
}
