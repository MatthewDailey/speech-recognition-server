package com.saypenis.speech.aws;

public final class SayPenisConfiguration {

	private SayPenisConfiguration() {}
	
	private static final String ENVIRONMENT_CONFIG_PARAM = "DEPLOYMENT_ENV";
	
	private enum Environment {
		PROD, STAGING, TEST;
	}
	
	private static Environment getDeploymentEnvironment() {
		return Environment.valueOf(
				System.getProperty(ENVIRONMENT_CONFIG_PARAM, Environment.TEST.name()));
	}
	
	private static final String ROUND_TABLE_PROD = "sp-round";
	private static final String ROUND_TABLE_STAGING = "test-sp-round-playable";
	private static final String ROUND_TABLE_TEST = "test-sp-round";
	
	public static String roundTable() {
		switch(getDeploymentEnvironment()) {
		case PROD:
			return ROUND_TABLE_PROD;
		case STAGING:
			return ROUND_TABLE_STAGING;
		case TEST:
			return ROUND_TABLE_TEST;
		default:
			throw new IllegalStateException("Unknown deployent environment.");
		}
	}

	private static final String ROUND_S3_BUCKET_PROD = "say-penis-clean-recordings/prod";
	private static final String ROUND_S3_BUCKET_STAGING = "say-penis-clean-recordings/staging";
	private static final String ROUND_S3_BUCKET_TEST = "say-penis-clean-recordings/test";
	
	public static String roundS3Bucket() {
		switch(getDeploymentEnvironment()) {
		case PROD:
			return ROUND_S3_BUCKET_PROD;
		case STAGING:
			return ROUND_S3_BUCKET_STAGING;
		case TEST:
			return ROUND_S3_BUCKET_TEST;
		default:
			throw new IllegalStateException("Unknown deployent environment.");
		}
	}
}
