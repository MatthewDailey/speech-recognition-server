package com.saypenis.speech.recognition;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.cmu.sphinx.api.Configuration;

@Singleton
public final class RecognitionServiceProvider {

	private final static Logger log = LoggerFactory.getLogger(RecognitionServiceProvider.class);
	
	private final static int MAX_ATTEMPTS_CREATE_RECOGNIZER = 10;

	private static RecognitionServiceProvider provider = new RecognitionServiceProvider();
	
	private RecognitionServiceProvider() {
		recognizerQueue = new LinkedBlockingDeque<PreAllocatingStreamSpeechRecognizer>();
		speechConfiguration = CmuSphinxRecognitionUtils.getAdaptedConfiguration();
		executor = Executors.newSingleThreadExecutor();
		enqueueNewRecognizerNonBlocking();
	}
	
	private final ExecutorService executor;
	private final BlockingQueue<PreAllocatingStreamSpeechRecognizer> recognizerQueue; 
	private final Configuration speechConfiguration;
	
	private final Runnable enqueueRecognizerRunnable = new Runnable() {
		@Override
		public void run() {
			for (int attemptNum = 0; attemptNum < MAX_ATTEMPTS_CREATE_RECOGNIZER; attemptNum++) {
				try {
					recognizerQueue.put(new PreAllocatingStreamSpeechRecognizer(speechConfiguration));
					return;
				} catch (Exception e) {
					log.error("Failed to enqueue new recognizer with exception {}", e, e);
				}
			}
		}
	};
	
	private void enqueueNewRecognizerNonBlocking() {
		executor.execute(enqueueRecognizerRunnable);
	}
	
	public static RecognitionService getCmuSphinxRecognizer() {
		RecognitionService result;
		try {
			result = new CmuSphinxRecognitionService(provider.recognizerQueue.poll(1, TimeUnit.MINUTES));
		} catch (InterruptedException interruptedException) {
			log.error("Failed to poll() RecognitionService from queue with exception {}", 
					interruptedException, interruptedException);
			result = buildRecognitionServiceSafe();
		}
		provider.enqueueNewRecognizerNonBlocking();
		return result;
	}
	
	private static RecognitionService buildRecognitionServiceSafe() {
		try {
			return new CmuSphinxRecognitionService(
					new PreAllocatingStreamSpeechRecognizer(provider.speechConfiguration));
		} catch (IOException ioException) {
			log.error("Failed to create new recognizer with exception {}", ioException, ioException);
			return new NoopRecognitionService();
		}
	}
	
}
