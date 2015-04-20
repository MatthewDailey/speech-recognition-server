package com.saypenis.speech.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Stopwatch;
import com.saypenis.speech.api.serialization.SerializationUtils;
import com.saypenis.speech.aws.AwsSupplier;
import com.saypenis.speech.recognition.RecognitionService;
import com.saypenis.speech.recognition.RecognitionServiceProvider;

import edu.cmu.sphinx.result.WordResult;

@Path("recognize/s3")
public class RecognizeS3Resource {

	private final static Logger log = LoggerFactory.getLogger(RecognizeS3Resource.class);
	
	private final RecognitionService recognitionService;
	private final AmazonS3 amazonS3;
	
	public RecognizeS3Resource() {
		this(RecognitionServiceProvider.getCmuSphinxRecognizer(), AwsSupplier.getS3());
	}
	
	public RecognizeS3Resource(RecognitionService recognitionService, AmazonS3 amazonS3) {
		this.recognitionService = recognitionService;
		this.amazonS3 = amazonS3;
	}
	
	@GET 
	@Produces("application/json")
	public void get(
			@Suspended final AsyncResponse asyncResponse,
			@QueryParam("s3bucket") final String s3bucket, 
			@QueryParam("s3file") final String s3key) throws IOException {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					asyncResponse.resume(Response.ok(
							SerializationUtils.serialize(processS3RecognizeRequest(s3bucket, s3key)))
							.build());
				} catch (IOException e) {
					log.error("Async s3recognize failed with IOException " + e);
					asyncResponse.resume("Fail with exception: " + e);
				} catch (JAXBException e) {
					log.error("Async s3recognize failed with JAXBException " + e);
					asyncResponse.resume("Fail with exception: " + e);
				}
			}
		}).start();
	}
	
	private List<WordResult> processS3RecognizeRequest(String s3bucket, String s3key) throws IOException {
		log.debug("Received request with params s3bucket={} and s3key={}", s3bucket, s3key);
		Stopwatch totalTimeStopwatch = Stopwatch.createStarted();
		
		Stopwatch s3FetchStopwatch = Stopwatch.createStarted();
    	S3Object file = amazonS3.getObject(s3bucket, s3key);
    	log.debug("Finished fetching {} from s3. Took {} ms.", s3key, s3FetchStopwatch.elapsed(TimeUnit.MILLISECONDS));
    	
    	List<WordResult> results = recognitionService.recognize(file.getObjectContent());
    	
    	log.debug("Completed request in {} ms", totalTimeStopwatch.elapsed(TimeUnit.MILLISECONDS));
		return results;		
	}

}
