package com.saypenis.speech.api.serialization;


public final class WordResultBean {
	
	public String word;
	public double score;
	
	public WordResultBean() {}
	
	public WordResultBean(String word, double score) {
		this.word = word;
		this.score = score;
	}

}
