package com.saypenis.speech.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.saypenis.speech.perf.PerfUtils;
import com.saypenis.speech.perf.PerfUtils.LoggingTimer;

@Path("/recognize/upload")
public class RecognizeUploadResource {
	
	private static final Logger log = LoggerFactory.getLogger(RecognizeUploadResource.class);

	/*
	 * Need args: 
	 * 1. Data stream to do recognition.
	 * 2. Player name.
	 * 3. Player id.
	 * 4. lat / lon.
	 * 
	 * Return:
	 * 1. score
	 * 2. s3 url
	 * 3. timestamp
	 * 4. round id
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response post(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDisposition) {
		String filename = fileDisposition.getFileName();
		
		log.debug("Received POST to /recognize/upload for file {}.", filename);
		LoggingTimer resourceTimer = PerfUtils.getTimerStarted("/recognize/upload file=" + filename);
		
		byte[] fileContents = new byte[0];
		try {
			fileContents = ByteStreams.toByteArray(fileInputStream);
		} catch (IOException e) {
			log.error("Failed to read fileinput stream with exception {}", e);
			return Response.status(200).entity("Failed to read file stream.").build();
		}
		log.debug("File {} has size {} bytes", filename, fileContents.length);
		
		// 1. Generate time stamp.
		long timestamp = System.currentTimeMillis();
		
		// 2. Generate id.
		UUID roundId = UUID.randomUUID();
		
		// 3. Genearate s3 url (from ids).
		String s3Url = generateS3Url();
		
		// 4. Do voice recognition and scoring.
		// 5. Send back response.
		// 6. Async store to S3. They'll play the file locally.
		// 7. Async store row dynamo. They'll add to the score list locally.
		
		resourceTimer.log();
		return Response.status(200).entity("Success").build();
	}
	
	private String generateS3Url() {
		// TODO (mdailey): Copy from android.
		// SayPenisAwsService -> logic for SayPenis tables and s3 paths.
		return "null";
	}
	
	private double scoreAudio(byte[] fileContents) {
		// TODO (mdailey): ScoringService -> gitignored for secrecy.
		return 0;
	}
}
