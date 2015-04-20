package com.saypenis.speech.aws;

import javax.inject.Singleton;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;

@Singleton
public final class AwsSupplier {

	private AwsSupplier() {}
	
	public static final AwsSupplier instance = new AwsSupplier();

	private static final String accessKey = System.getProperty("AWS_ACCESS_KEY_ID");
	private static final String secretKey = System.getProperty("AWS_SECRET_KEY");
	
	private AmazonS3 s3;
	private TransferManager transferManager;
	private AmazonDynamoDBAsync dynamo;

	private static AWSCredentials getCreds() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	public static TransferManager getTransferManager() {
		return instance.transferManager();
	}

	private synchronized TransferManager transferManager() {
		if (transferManager == null) {
			transferManager = new TransferManager(getCreds());
		}
		return transferManager;
	}
	
	public static AmazonS3 getS3() {
		return instance.s3();
	}

	private synchronized AmazonS3 s3() {
		if (s3 == null) {
			s3 = new AmazonS3Client(getCreds());
		}
		return s3;
	}
	
	public static AmazonDynamoDBAsync getAsyncDynamo() {
		return instance.dynamo();
	}

	private synchronized AmazonDynamoDBAsync dynamo() {
		if (dynamo == null) {
			dynamo = new AmazonDynamoDBAsyncClient(getCreds());
		}
		return dynamo;
	}
}