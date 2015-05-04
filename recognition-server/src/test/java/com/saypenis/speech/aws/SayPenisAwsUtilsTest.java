package com.saypenis.speech.aws;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.collect.Maps;
import com.saypenis.speech.api.serialization.RoundBean;
import com.saypenis.speech.aws.AwsSupplier;
import com.saypenis.speech.aws.SayPenisAwsUtils;
import com.saypenis.speech.aws.SayPenisConfiguration;

public class SayPenisAwsUtilsTest {

	@Test
	public void testDynamoPut() {
		RoundBean beanToStore = new RoundBean("test_round_id", 10, 5, 5, "test_name", 
				99, "test_s3bucket", "test_s3key", "test_user_id", "test transcription");
		
		SayPenisAwsUtils.storeToDynamoAsync(SayPenisConfiguration.roundTable(), beanToStore, 
				AwsSupplier.getDynamo());

		// Fetch from dynamo.
		AmazonDynamoDB dynamo = AwsSupplier.getDynamo();
		Map<String, AttributeValue> getItemMap = Maps.newHashMapWithExpectedSize(1);
		getItemMap.put("round_id", new AttributeValue().withS("test_round_id"));
		GetItemResult result = dynamo.getItem(new GetItemRequest(SayPenisConfiguration.roundTable(), 
				getItemMap));
		Map<String, AttributeValue> resultMap = result.getItem();
		assertEquals("test transcription", resultMap.get("transcription").getS());
	}
	
	@Test
	public void testS3Put() throws AmazonServiceException, AmazonClientException, IOException, InterruptedException {
		String fileContents = "file contents string";
		byte[] fileContentsBytes = fileContents.getBytes();
		String s3key = "SayPenisAwsUtilsTest.txt";
		Upload upload = SayPenisAwsUtils.storeToS3Async(SayPenisConfiguration.roundS3Bucket(), s3key, fileContentsBytes,
				AwsSupplier.getTransferManager());
		
		upload.waitForCompletion();
		
		byte[] resultBytes = new byte[fileContentsBytes.length];
		AwsSupplier.getS3().getObject(SayPenisConfiguration.roundS3Bucket(), s3key)
			.getObjectContent().read(resultBytes);
		
		assertTrue(Arrays.equals(fileContentsBytes, resultBytes));
	}
}
