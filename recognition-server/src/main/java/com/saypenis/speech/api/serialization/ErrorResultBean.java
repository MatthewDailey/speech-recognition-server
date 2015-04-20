package com.saypenis.speech.api.serialization;

public class ErrorResultBean extends ResultBean {

	public final int resultCode;
	
	public ErrorResultBean(int resultCode) {
		super(false /*success*/);
		this.resultCode = resultCode;
	}
	
}
