package com.saypenis.speech.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.saypenis.speech.SayPenisConstants;
import com.saypenis.speech.api.serialization.RoundResultBean;
import com.saypenis.speech.perf.PerfUtils;
import com.saypenis.speech.perf.PerfUtils.LoggingTimer;
import com.saypenis.speech.recognition.RecognitionServiceProvider;
import com.saypenis.speech.scoring.SayPenisScoringUtils;

import edu.cmu.sphinx.result.WordResult;

@Path("/recognize/upload")
public class RecognizeUploadResource {
	
	private static final Logger log = LoggerFactory.getLogger(RecognizeUploadResource.class);

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void post(
			@Suspended final AsyncResponse asyncResponse,
			@FormDataParam("file") final InputStream fileInputStream,
			@FormDataParam("file") final FormDataContentDisposition fileDisposition,
			@DefaultValue(SayPenisConstants.DEFAULT_LAT) @FormDataParam("lat") final long lat,
			@DefaultValue(SayPenisConstants.DEFAULT_LON) @FormDataParam("lon") final long lon,
			@DefaultValue(SayPenisConstants.DEFAULT_USER_ID) @FormDataParam("user_id") final String userId,
			@FormDataParam("name") final String name) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final String filename = fileDisposition.getFileName();
					final byte[] fileContents = ByteStreams.toByteArray(fileInputStream);
					
					RoundResultBean result = handlePost(fileContents, filename, lat, lon, 
							userId, name);
					Gson gson = new Gson();
					asyncResponse.resume(Response.ok(gson.toJson(result)).build());
					
					// Store result 
					// Store to s3
				} catch (IOException e) {
					log.error("Async  failed with IOException " + e);
					asyncResponse.resume("Fail with exception: " + e);
				}
			}
		}).start();
	}
	
	private RoundResultBean handlePost(			
			final byte[] fileContents,
			final String filename,
			final long lat,
			final long lon,
			final String userId,
			final String name) throws IOException {
		log.debug("Received POST to /recognize/upload for file {}.", filename);
		log.debug("File {} has size {} bytes", filename, fileContents.length);
		LoggingTimer resourceTimer = PerfUtils.getTimerStarted("/recognize/upload file=" + filename);

		// Generate timestamp, roundId, s3key
		long date = System.currentTimeMillis();
		UUID roundId = UUID.randomUUID();
		String s3Key = "round_" + roundId.toString();
		
		List<WordResult> wordResults = RecognitionServiceProvider.getCmuSphinxRecognizer()
				.recognize(new ByteArrayInputStream(fileContents));

		Optional<Double> score = SayPenisScoringUtils.getScore(wordResults);
		
		RoundResultBean roundResultBean = new RoundResultBean(roundId.toString(), date, lat, lon, 
				name, 0, s3Key, userId, "no transcription");
		
		resourceTimer.log();	
		return roundResultBean;
	}
	
}
