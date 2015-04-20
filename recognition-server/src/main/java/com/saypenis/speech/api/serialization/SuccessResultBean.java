package com.saypenis.speech.api.serialization;

public class SuccessResultBean implements ResultBean {

	public final String round_id;
	public final long date;
	public final long lat;
	public final long lon;
	public final String name;
	public final double score;
	public final String uri;
	public final String user_id;
	public final String transcription;
	
	public SuccessResultBean(String round_id, long date, long lat, long lon,
			String name, double score, String uri, String user_id,
			String transcription) {
		this.round_id = round_id;
		this.date = date;
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.score = score;
		this.uri = uri;
		this.user_id = user_id;
		this.transcription = transcription;
	}
	
}
