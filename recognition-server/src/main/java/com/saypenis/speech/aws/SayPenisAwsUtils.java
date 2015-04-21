package com.saypenis.speech.aws;

import java.io.ByteArrayInputStream;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.common.collect.ImmutableMap;
import com.saypenis.speech.api.serialization.SuccessResultBean;

public final class SayPenisAwsUtils {

	private SayPenisAwsUtils() {}
	
	private static final int VALID = 1;
	
	public static void storeToS3Async(String s3bucket, String s3Key, byte[] fileContents, 
			TransferManager transferManager) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(fileContents.length);
		transferManager.upload(s3bucket, s3Key, new ByteArrayInputStream(fileContents), objectMetadata);
	}
	
	public static void storeToDynamoAsync(String tableName, SuccessResultBean resultBean, 
			AmazonDynamoDB dynamo) {
		dynamo.putItem(tableName, successBeanToAttributeValues(resultBean));
	}
	
	public static Map<String, AttributeValue> successBeanToAttributeValues(SuccessResultBean resultBean) {
		ImmutableMap.Builder<String, AttributeValue> columnToAttributeValue = ImmutableMap.builder();
		
		String uri = getS3Uri(resultBean.s3bucket, resultBean.s3key);
		columnToAttributeValue.put("round_id", new AttributeValue().withS(resultBean.round_id));
		columnToAttributeValue.put("score", 	new AttributeValue().withN(String.valueOf(resultBean.score)));
		columnToAttributeValue.put("date", 	new AttributeValue().withN(String.valueOf(resultBean.date)));
		columnToAttributeValue.put("lat", 		new AttributeValue().withN(String.valueOf(resultBean.lat)));
		columnToAttributeValue.put("lon", 		new AttributeValue().withN(String.valueOf(resultBean.lon)));
		columnToAttributeValue.put("user_id",  new AttributeValue().withS(resultBean.user_id));
		columnToAttributeValue.put("name", 	new AttributeValue().withS(resultBean.name));
		columnToAttributeValue.put("uri", 		new AttributeValue().withS(uri));
		columnToAttributeValue.put("valid", 	new AttributeValue().withN(String.valueOf(VALID)));
		columnToAttributeValue.put("transcription", new AttributeValue().withS(resultBean.transcription));
		
		return columnToAttributeValue.build();
	}
	
	private static String getS3Uri(String s3bucket, String s3key) {
		return "https://s3.amazonaws.com/" + s3bucket + "/" + s3key;
	}
}
