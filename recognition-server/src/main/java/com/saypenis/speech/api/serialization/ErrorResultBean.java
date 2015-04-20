package com.saypenis.speech.api.serialization;

public class ErrorResultBean implements ResultBean {

	public final int resultCode;
	public final String message;
	
	public ErrorResultBean(int resultCode, String message) {
		this.resultCode = resultCode;
		this.message = message;
	}
	
}
