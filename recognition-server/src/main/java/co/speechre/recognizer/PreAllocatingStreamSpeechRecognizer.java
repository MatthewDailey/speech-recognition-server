package co.speechre.recognizer;

import java.io.IOException;
import java.io.InputStream;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.util.TimeFrame;

public class PreAllocatingStreamSpeechRecognizer extends AbstractSpeechRecognizer {

	public PreAllocatingStreamSpeechRecognizer(Configuration configuration)
			throws IOException {
		super(configuration);
		recognizer.allocate();
	}

    public void startRecognition(InputStream stream) {
        startRecognition(stream, TimeFrame.INFINITE);
    }

    public void startRecognition(InputStream stream, TimeFrame timeFrame) {
        context.setSpeechSource(stream, timeFrame);
    }

    public void stopRecognition() {
        recognizer.deallocate();
    }
}
