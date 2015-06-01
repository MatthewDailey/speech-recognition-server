package com.saypenis.speech.api.serialization;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoundBean extends ResultBean {

	public final String round_id;
	public final long date;
	public final String dateString;
	public final double lat;
	public final double lon;
	public final String name;
	public final double score;
	public final String uri;
	public final String user_id;
	public final String transcription;
	
	public RoundBean(String round_id, long date, double lat, double lon,
			String name, double score, String uri, String user_id,
			String transcription) {
		super(true /* success */);
		this.round_id = round_id;
		this.date = date;
		this.dateString = convertTime(date);
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.score = score;
		this.uri = uri;
		this.user_id = user_id;
		this.transcription = transcription;
	}
	
	private String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
	    return format.format(date);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (date ^ (date >>> 32));
		result = prime * result
				+ ((dateString == null) ? 0 : dateString.hashCode());
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((round_id == null) ? 0 : round_id.hashCode());
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((transcription == null) ? 0 : transcription.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((user_id == null) ? 0 : user_id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoundBean other = (RoundBean) obj;
		if (date != other.date)
			return false;
		if (dateString == null) {
			if (other.dateString != null)
				return false;
		} else if (!dateString.equals(other.dateString))
			return false;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (round_id == null) {
			if (other.round_id != null)
				return false;
		} else if (!round_id.equals(other.round_id))
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		if (transcription == null) {
			if (other.transcription != null)
				return false;
		} else if (!transcription.equals(other.transcription))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (user_id == null) {
			if (other.user_id != null)
				return false;
		} else if (!user_id.equals(other.user_id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RoundBean [round_id=" + round_id + ", date=" + date
				+ ", dateString=" + dateString + ", lat=" + lat + ", lon="
				+ lon + ", name=" + name + ", score=" + score + ", uri=" + uri
				+ ", user_id=" + user_id + ", transcription=" + transcription
				+ "]";
	}

}
