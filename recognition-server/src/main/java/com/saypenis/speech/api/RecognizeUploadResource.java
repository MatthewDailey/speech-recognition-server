package com.saypenis.speech.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/recognize/upload")
public class RecognizeUploadResource {
	
	private static final Logger log = LoggerFactory.getLogger(RecognizeUploadResource.class);

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response post(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDisposition) {
		log.debug("Received POST to /recognize/upload for file {} with size {}.", 
				fileDisposition.getFileName(), fileDisposition.getSize());
		
		log.debug("Finished /recognize/upload for file {}.", fileDisposition.getFileName());
		return Response.status(200).entity("Success").build();
	}
	
}
