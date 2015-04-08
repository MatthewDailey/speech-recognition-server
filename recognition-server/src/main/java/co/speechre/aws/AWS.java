package co.speechre.aws;

import javax.inject.Singleton;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

@Singleton
public final class AWS {
	
	private AWS() {}

	public static final AWS services = new AWS();
	
	private static final String accessKey = System.getProperty("AWS_ACCESS_KEY_ID");
	private static final String secretKey = System.getProperty("AWS_SECRET_KEY");
	
	private AmazonS3 s3;
	private AmazonDynamoDB dynamo;

	private AWSCredentials getCreds() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	public synchronized AmazonS3 s3() {
		if (s3 == null) {
			s3 = new AmazonS3Client(getCreds());
		}
		return s3;
	}

	public synchronized AmazonDynamoDB dynamo() {
		if (dynamo == null) {
			dynamo = new AmazonDynamoDBClient(getCreds());
		}
		return dynamo;
	}
}