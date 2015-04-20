package com.saypenis.speech.aws;

import java.io.ByteArrayInputStream;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.saypenis.speech.api.serialization.SuccessResultBean;

public final class SayPenisAwsUtils {

	private SayPenisAwsUtils() {}
	
	public static void storeToS3Async(String s3bucket, String s3Key, byte[] fileContents, 
			TransferManager transferManager) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(fileContents.length);
		transferManager.upload(s3bucket, s3Key, new ByteArrayInputStream(fileContents), objectMetadata);
	}
	
	public static void storeToDynamoAsync(String tableName, SuccessResultBean resultBean, 
			AmazonDynamoDBAsync dynamo) {
		
	}
	
}
