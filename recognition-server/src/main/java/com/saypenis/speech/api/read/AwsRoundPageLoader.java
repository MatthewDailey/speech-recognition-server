package com.saypenis.speech.api.read;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.google.common.base.Optional;
import com.saypenis.speech.api.serialization.RoundBean;
import com.saypenis.speech.aws.SayPenisAwsUtils;

/**
 * Page loader which scans the dynamo table. This is intended for loading a single large page
 * to cache rather than on-the-fly request handling.
 * 
 * @author mdailey
 */
public class AwsRoundPageLoader implements PageLoader<RoundBean> {

	private final AmazonDynamoDB dynamoDb;
	private final String table;
	private final Optional<String> index;
	
	public AwsRoundPageLoader(AmazonDynamoDB dynamoDb, String table, Optional<String> index) {
		this.dynamoDb = dynamoDb;
		this.table = table;
		this.index = index;
	}
	
	@Override
	public RoundBean[] getPage(int page, int pageSize) {
		int maxIndexNeeded = (page * pageSize) + pageSize;
		
		QueryResult query = dynamoDb.query(getTopNRequest(maxIndexNeeded));
		
		List<Map<String, AttributeValue>> items = query.getItems();
		
		int startIndex = page * pageSize;
		int numResults = Math.max(0,  items.size() - startIndex);
		RoundBean[] roundBeans = new RoundBean[numResults];
		for (int i = 0; i < numResults; i++) {
			int itemIndex = startIndex + i;
			roundBeans[i] = SayPenisAwsUtils.attributeValuesToRoundBean(items.get(itemIndex));
		}
		return roundBeans;
	}
	
	private QueryRequest getTopNRequest(int n) {
		QueryRequest request = new QueryRequest(table);
		if (index.isPresent()) {
			request.withIndexName(index.get());
		}
		request.setLimit(n);
		request.addKeyConditionsEntry("valid", equalsConditions(1));
		request.withScanIndexForward(false);
		return request;
	}
	
	private Condition equalsConditions(int value) {
		return new Condition()
			.withComparisonOperator(ComparisonOperator.EQ)
			.withAttributeValueList(new AttributeValue().withN(String.valueOf(value)));
	}

}
