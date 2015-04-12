package com.saypenis.speech.perf;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public final class PerfUtils {

	private static final Logger perfLog = LoggerFactory.getLogger(PerfUtils.class);
	
	private PerfUtils() {}
	
	public static class LoggingTimer {
		private final Stopwatch stopwatch;
		private final String call;
		
		public LoggingTimer(String call) {
			this.stopwatch = Stopwatch.createStarted();
			this.call = call;
		}
		
		public void log() {
			perfLog.debug("[perf] call {} took {} ms.", call, stopwatch.elapsed(TimeUnit.MILLISECONDS));
		}
	}
	
	public static LoggingTimer getTimerStarted(String call) {
		return new LoggingTimer(call);
	}
	
}
