package com.saypenis.speech;

public final class SayPenisConstants {

	private SayPenisConstants() {}
	
	public static final String DEFAULT_USER_ID = "default-user";
	// TODO (mdailey): Make these an interesting place near SF?
	public static final String DEFAULT_LAT = "-1";
	public static final String DEFAULT_LON = "-1";
	
	// Error result codes.
	public static final int ERROR_INTERNAL_ERROR = 0;
	public static final int ERROR_SERVICE_OFFLINE = 1;
	public static final int ERROR_NO_PENIS_DETECTED = 2;
	public static final int ERROR_TOO_MANY_PENISES = 3;
	public static final int ERROR_NON_PENIS_WORDS = 4;
	
}
