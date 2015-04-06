package co.speechre.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public final class AWS {

	private AWS() {}

	private static final String accessKey = System.getProperty("AWS_ACCESS_KEY_ID");
	private static final String secretKey = System.getProperty("AWS_SECRET_KEY");
	
	private static AmazonS3 s3;
	private static AmazonDynamoDB dynamo;

	private static AWSCredentials getCreds() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	public static AmazonS3 getS3() {
		if (s3 == null) {
			s3 = new AmazonS3Client(getCreds());
		}
		return s3;
	}

	public static AmazonDynamoDB getDynamoDb() {
		if (dynamo == null) {
			dynamo = new AmazonDynamoDBClient(getCreds());
		}
		return dynamo;
	}
}