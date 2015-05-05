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
	private static final String S3_URI_PREFIX = "https://s3.amazonaws.com/";
	
	public static Upload storeToS3Async(String s3Uri, byte[] fileContents, 
			TransferManager transferManager) {
		String s3bucket = getS3Bucket(s3Uri);
		String s3key = getS3Key(s3Uri);
		
		log.debug("Storing to S3 bucket={} key={}", s3bucket, s3key);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(fileContents.length);
		return transferManager.upload(s3bucket, s3key, new ByteArrayInputStream(fileContents), objectMetadata);
	}
	
	public static void storeToDynamoAsync(String tableName, RoundBean resultBean, 
			AmazonDynamoDB dynamo) {
		dynamo.putItem(tableName, successBeanToAttributeValues(resultBean));
	}
	
	public static Map<String, AttributeValue> successBeanToAttributeValues(RoundBean resultBean) {
		ImmutableMap.Builder<String, AttributeValue> columnToAttributeValue = ImmutableMap.builder();

		columnToAttributeValue.put("round_id", new AttributeValue().withS(resultBean.round_id));
		columnToAttributeValue.put("score", 	new AttributeValue().withN(String.valueOf(resultBean.score)));
		columnToAttributeValue.put("date", 	new AttributeValue().withN(String.valueOf(resultBean.date)));
		columnToAttributeValue.put("lat", 		new AttributeValue().withN(String.valueOf(resultBean.lat)));
		columnToAttributeValue.put("lon", 		new AttributeValue().withN(String.valueOf(resultBean.lon)));
		columnToAttributeValue.put("user_id",  new AttributeValue().withS(resultBean.user_id));
		columnToAttributeValue.put("name", 	new AttributeValue().withS(resultBean.name));
		columnToAttributeValue.put("uri", 		new AttributeValue().withS(resultBean.uri));
		columnToAttributeValue.put("valid", 	new AttributeValue().withN(String.valueOf(VALID)));
		columnToAttributeValue.put("transcription", new AttributeValue().withS(resultBean.transcription));
		
		return columnToAttributeValue.build();
	}
	
	public static RoundBean attributeValuesToRoundBean(Map<String, AttributeValue> attributeValues) {
		AttributeValue transcriptionAttribute = attributeValues.get("transcription");
		return new RoundBean(attributeValues.get("round_id").getS(),
							 Long.valueOf(attributeValues.get("date").getN()), 
							 Long.valueOf(attributeValues.get("lat").getN()), 
							 Long.valueOf(attributeValues.get("lon").getN()), 
							 attributeValues.get("name").getS(), 
							 Double.valueOf(attributeValues.get("score").getN()), 
							 attributeValues.get("uri").getS(), 
							 attributeValues.get("user_id").getS(), 
							 transcriptionAttribute == null ? "" : transcriptionAttribute.getS());
	}
	
	public static String getS3Uri(String s3bucket, String s3key) {
		return  S3_URI_PREFIX + s3bucket + "/" + s3key;
	}
	
	private static String getS3Bucket(String s3Uri) {
		return s3Uri.replace(S3_URI_PREFIX, "").replaceFirst("/.*", "");
	}

	private static String getS3Key(String s3Uri) {
		return s3Uri.replaceAll(".*/", "");
	}
}
