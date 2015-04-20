package com.saypenis.speech.scoring;

import java.util.List;

import com.google.common.base.Optional;
import com.saypenis.speech.SayPenisConstants;

import edu.cmu.sphinx.result.WordResult;

public final class SayPenisScoringUtils {

	private SayPenisScoringUtils() {}
	
	public static Optional<Double> getScore(List<WordResult> wordResults) {
		return Optional.absent();
	}

	public static int getErrorResultCode(List<WordResult> wordResults) {
		int penisesCount = 0;
		for (WordResult wordResult : wordResults) {
			if (wordResult.getWord().getSpelling().equalsIgnoreCase("penis")) {
				penisesCount++;
			}
		}
		
		switch(penisesCount) {
		case 0:
			return SayPenisConstants.ERROR_NO_PENIS_DETECTED;
		case 2:
			return SayPenisConstants.ERROR_TOO_MANY_PENISES;
		default:
			return SayPenisConstants.ERROR_INTERNAL_ERROR;
		}
	}
}
