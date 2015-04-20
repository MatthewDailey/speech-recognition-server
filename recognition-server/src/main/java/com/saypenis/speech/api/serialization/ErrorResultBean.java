package com.saypenis.speech.api.serialization;

public class ErrorResultBean implements ResultBean {

	public final int resultCode;
	
	public ErrorResultBean(int resultCode) {
		this.resultCode = resultCode;
	}
	
}
