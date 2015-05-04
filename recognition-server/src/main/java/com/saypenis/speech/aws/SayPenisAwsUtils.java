package com.saypenis.speech.aws;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.collect.ImmutableMap;
import com.saypenis.speech.api.serialization.RoundBean;

public final class SayPenisAwsUtils {

	private static final Logger log = LoggerFactory.getLogger(SayPenisAwsUtils.class);
	
	private SayPenisAwsUtils() {}
	
	private static final int VALID = 1;
	
	public static Upload storeToS3Async(String s3bucket, String s3Key, byte[] fileContents, 
			TransferManager transferManager) {
		log.debug("Storing to S3 bucket={} key={}", s3bucket, s3Key);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(fileContents.length);
		return transferManager.upload(s3bucket, s3Key, new ByteArrayInputStream(fileContents), objectMetadata);
	}
	
	public static void storeToDynamoAsync(String tableName, RoundBean resultBean, 
			AmazonDynamoDB dynamo) {
		dynamo.putItem(tableName, successBeanToAttributeValues(resultBean));
	}
	
	public static Map<String, AttributeValue> successBeanToAttributeValues(RoundBean resultBean) {
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
