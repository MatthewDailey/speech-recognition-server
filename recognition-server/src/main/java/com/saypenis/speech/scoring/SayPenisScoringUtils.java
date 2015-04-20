package com.saypenis.speech.scoring;

import java.util.List;

import com.google.common.base.Optional;

import edu.cmu.sphinx.result.WordResult;

public final class SayPenisScoringUtils {

	private SayPenisScoringUtils() {}
	
	public static Optional<Double> getScore(List<WordResult> wordResults) {
		return Optional.absent();
	}
	
}
