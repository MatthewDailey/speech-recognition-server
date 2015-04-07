package com.saypenis.speech.api.serialization;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class WordResultBean {
	
	public String word;
	public double score;
	
	public WordResultBean() {}
	
	public WordResultBean(String word, double score) {
		this.word = word;
		this.score = score;
	}

}
