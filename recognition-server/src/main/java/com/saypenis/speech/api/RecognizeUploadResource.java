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
import com.saypenis.speech.api.StatusResource.EndpointStatus;
import com.saypenis.speech.api.serialization.ErrorResultBean;
import com.saypenis.speech.api.serialization.ResultBean;
import com.saypenis.speech.api.serialization.SuccessResultBean;
import com.saypenis.speech.api.serialization.SerializationUtils;
import com.saypenis.speech.aws.AwsSupplier;
import com.saypenis.speech.aws.SayPenisAwsUtils;
import com.saypenis.speech.aws.SayPenisConfiguration;
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
			@Suspended AsyncResponse asyncResponse,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDisposition,
			@DefaultValue(SayPenisConstants.DEFAULT_LAT) @FormDataParam("lat") long lat,
			@DefaultValue(SayPenisConstants.DEFAULT_LON) @FormDataParam("lon") long lon,
			@DefaultValue(SayPenisConstants.DEFAULT_USER_ID) @FormDataParam("user_id") String userId,
			@FormDataParam("name") String name) {
		
		if (StatusResource.getStatus() == EndpointStatus.READ_WRITE) {
			launchAsyncResponseThread(asyncResponse, fileInputStream, fileDisposition, lat, lon, userId, name);
		} else {
			respondErrorReadOnly(asyncResponse);
		}
	}
	
	private void launchAsyncResponseThread(final AsyncResponse asyncResponse,
			final InputStream fileInputStream, final FormDataContentDisposition fileDisposition,
			final long lat, final long lon, final String userId, final String name) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Gson gson = new Gson();
				try {
					final byte[] fileContents = ByteStreams.toByteArray(fileInputStream);
					
					ResultBean result = createRoundMetadataAndScoreAudio(fileContents, lat, lon, 
							userId, name);
					
					asyncResponse.resume(Response.ok(gson.toJson(result)).build());
					
					if (result.success) {
						writeResultToDynamoAndS3(result, fileContents);
					}
				} catch (Exception e) {
					responseInternalError(asyncResponse, e);
				}
			}
		}).start();
	}
	
	private void respondErrorReadOnly(AsyncResponse asyncResponse) {
		log.warn("Request to /recognize/upload rejected due to status={}", EndpointStatus.READ_ONLY);
		Gson gson = new Gson();
		ErrorResultBean errorResultBean = new ErrorResultBean(
				SayPenisConstants.ERROR_SERVICE_READ_ONLY);
		asyncResponse.resume(Response.ok(gson.toJson(errorResultBean)).build());
	}
	
	private void responseInternalError(AsyncResponse asyncResponse, Exception e) {
		log.error("Async /recognize/upload failed with Exception {}", e, e);
		Gson gson = new Gson();
		ErrorResultBean errorResultBean = new ErrorResultBean(
				SayPenisConstants.ERROR_INTERNAL_ERROR);
		asyncResponse.resume(Response.ok(gson.toJson(errorResultBean)).build());
	}
	
	private void writeResultToDynamoAndS3(ResultBean result, byte[] fileContents) {
		SuccessResultBean successBean = (SuccessResultBean) result;
		try {
			SayPenisAwsUtils.storeToDynamoAsync(SayPenisConfiguration.roundTable(), 
					successBean, AwsSupplier.getDynamo());
			SayPenisAwsUtils.storeToS3Async(successBean.s3bucket, 
					successBean.s3key, fileContents, AwsSupplier.getTransferManager());
		} catch (Exception e) {
			log.error("Failed to store result to AWS for result={} with exception={}", result, e, e);
		}
	}
	
	private ResultBean createRoundMetadataAndScoreAudio(byte[] fileContents, long lat, long lon,
			String userId, String name) throws IOException {
		long date = System.currentTimeMillis();
		UUID roundId = UUID.randomUUID();
		String s3Key = "round_" + roundId.toString();
		
		log.debug("Received POST to /recognize/upload for round {}.", roundId);
		log.debug("Round {} has size {} bytes", roundId, fileContents.length);
		LoggingTimer resourceTimer = PerfUtils.getTimerStarted("/recognize/upload round=" + roundId);
		
		List<WordResult> wordResults = RecognitionServiceProvider.getCmuSphinxRecognizer()
				.recognize(new ByteArrayInputStream(fileContents));
		log.debug("Word results for round {} : {}", roundId, wordResults);
		
		Optional<Double> score = SayPenisScoringUtils.getScore(wordResults);
		
		String transcription = SerializationUtils.transcribe(wordResults);
		
		resourceTimer.log();
		if (score.isPresent()) {
			return new SuccessResultBean(roundId.toString(), date, lat, lon, name, score.get(), 
					SayPenisConfiguration.roundS3Bucket(), s3Key, userId, transcription);
		} else {
			int resultCode = SayPenisScoringUtils.getErrorResultCode(wordResults);
			return new ErrorResultBean(resultCode);
		}
	}
	
}
