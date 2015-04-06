package co.speechre.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.speechre.api.serialization.WordResultBean;
import co.speechre.aws.AWS;

import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.LogMath;

@Path("s3recognize")
public class S3Recognize {

	private final static Logger log = LoggerFactory.getLogger("s3recognize");
	
	@GET 
	@Produces("application/json")
	public String get(@QueryParam("s3bucket") String s3bucket, 
					  @QueryParam("s3file") String s3key) throws IOException {
		log.debug("Received request with params s3bucket={} and s3key={}", s3bucket, s3key);
		
		Stopwatch createRecognizerStopwatch = Stopwatch.createStarted();
    	Configuration configuration = new Configuration();
    	configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
    	configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
    	configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");
    	
    	StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
    	log.debug("Finished creating recognized. It took {} ms", createRecognizerStopwatch.elapsed(TimeUnit.MILLISECONDS));

    	Stopwatch s3FetchStopwatch = Stopwatch.createStarted();
    	S3Object file = AWS.getS3().getObject(s3bucket, s3key);
    	log.debug("Finished fetching {} from s3. Took {} ms.", s3key, s3FetchStopwatch.elapsed(TimeUnit.MILLISECONDS));
    	
    	Stopwatch recognizeStopwatch = Stopwatch.createStarted();
    	recognizer.startRecognition(file.getObjectContent());
    	SpeechResult result = recognizer.getResult();    	
    	recognizer.stopRecognition();
    	log.debug("Finished recognition. Took {} ms.", recognizeStopwatch.elapsed(TimeUnit.MILLISECONDS));
    	
    	List<WordResultBean> beans = Lists.newArrayList();
    	for (WordResult wr : result.getWords()) {
    		log.info(wr.toString());
    		beans.add(new WordResultBean(wr.getWord().toString(),  
    				LogMath.getLogMath().logToLinear((float)wr.getConfidence())));
    	}
		
    	log.debug("Completed request!");
		return "Success";
	}
}
