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

	private static RecognitionServiceProvider provider = new RecognitionServiceProvider();
	
	private RecognitionServiceProvider() {
		recognizerQueue = new LinkedBlockingDeque<PreAllocatingStreamSpeechRecognizer>();
		speechConfiguration = CmuSphinxRecognitionUtils.getConfiguration();
		executor = Executors.newSingleThreadExecutor();
		enqueueNewRecognizerNonBlocking();
	}
	
	private final ExecutorService executor;
	private final BlockingQueue<PreAllocatingStreamSpeechRecognizer> recognizerQueue; 
	private final Configuration speechConfiguration;
	
	private final Runnable enqueueRecognizerRunnable = new Runnable() {
		@Override
		public void run() {
			while(true) {
				try {
					recognizerQueue.put(new PreAllocatingStreamSpeechRecognizer(speechConfiguration));
					break;
				} catch (Exception e) {
					log.error("Failed to enqueue new recognizer with exception {}", e);
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
		} catch (InterruptedException e) {
			log.error("Failed to take() RecognitionService from queue with exception {}", e);
			try {
				result = new CmuSphinxRecognitionService(
						new PreAllocatingStreamSpeechRecognizer(provider.speechConfiguration));
			} catch (IOException e1) {
				log.error("Failed to create new recognizer with exception {}", e1);
				// TODO (mdailey): Do this better.
				result = null;
			}
		}
		provider.enqueueNewRecognizerNonBlocking();
		return result;
	}
	
}
