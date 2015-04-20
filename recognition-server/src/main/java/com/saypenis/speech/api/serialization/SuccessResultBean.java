package com.saypenis.speech.api.serialization;

public class SuccessResultBean extends ResultBean {

	public final String round_id;
	public final long date;
	public final long lat;
	public final long lon;
	public final String name;
	public final double score;
	public final String s3bucket;
	public final String s3key;
	public final String user_id;
	public final String transcription;
	
	public SuccessResultBean(String round_id, long date, long lat, long lon,
			String name, double score, String s3bucket, String s3key, String user_id,
			String transcription) {
		super(true /* success */);
		this.round_id = round_id;
		this.date = date;
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.score = score;
		this.s3bucket = s3bucket;
		this.s3key = s3key;
		this.user_id = user_id;
		this.transcription = transcription;
	}
	
}
