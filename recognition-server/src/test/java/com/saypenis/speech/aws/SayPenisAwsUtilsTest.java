package com.saypenis.speech.aws;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.google.common.collect.Maps;
import com.saypenis.speech.api.serialization.SuccessResultBean;

public class SayPenisAwsUtilsTest {

	@Test
	public void testAsyncDynamoPut() {
		SuccessResultBean beanToStore = new SuccessResultBean("test_round_id", 10, 5, 5, "test_name", 
				99, "test_s3bucket", "test_s3key", "test_user_id", "test transcription");
		
		SayPenisAwsUtils.storeToDynamoAsync(SayPenisConfiguration.roundTable(), beanToStore, 
				AwsSupplier.getDynamo());

		AmazonDynamoDB dynamo = AwsSupplier.getDynamo();
		Map<String, AttributeValue> getItemMap = Maps.newHashMapWithExpectedSize(1);
		getItemMap.put("round_id", new AttributeValue().withS("test_round_id"));
		GetItemResult result = dynamo.getItem(new GetItemRequest(SayPenisConfiguration.roundTable(), 
				getItemMap));
		Map<String, AttributeValue> resultMap = result.getItem();
		assertEquals("test transcription", resultMap.get("transcription").getS());
	}
}
