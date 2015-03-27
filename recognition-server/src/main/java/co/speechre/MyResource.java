package co.speechre;

import java.io.FileInputStream;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.speechre.recognizer.PreAllocatingStreamSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

	private final static Logger log = LoggerFactory.getLogger("myresource");
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     * @throws IOException 
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() throws IOException {
    	Configuration configuration = new Configuration();
    	 
    	// Set path to acoustic model.
    	configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
    	// Set path to dictionary.
    	configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
    	// Set language model.
    	configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");
    	
    	PreAllocatingStreamSpeechRecognizer recognizer = new PreAllocatingStreamSpeechRecognizer(configuration);

    	recognizer.startRecognition(new FileInputStream("src/main/resources/test.wav"));
    	SpeechResult result = recognizer.getResult();    	
    	recognizer.stopRecognition();
    	
    	for (WordResult wr : result.getWords()) {
    		log.info(wr.toString());
    	}
    	
    	return "Got it!";
        
    }
}
