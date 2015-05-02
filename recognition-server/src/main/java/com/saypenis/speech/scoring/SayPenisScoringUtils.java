package com.saypenis.speech.scoring;

import java.util.List;

import com.google.common.base.Optional;
import com.saypenis.speech.SayPenisConstants;

import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.LogMath;

public final class SayPenisScoringUtils {

	private SayPenisScoringUtils() {}

	private interface ScoreDampingFunction {
		double damp(double score);
	}
	
	private static class PowerDampingFunction implements ScoreDampingFunction {
		@Override
		public double damp(double score) {
			return Math.pow(score, 16);
		}
	}
	
	private static class LinearDampingFunction implements ScoreDampingFunction {
		@Override
		public double damp(double score) {
			return score - 0.001;
		}
	}
	
	private static double scoreFromWordResult(WordResult wordResult) {
		ScoreDampingFunction linearDampingFunction = new LinearDampingFunction();
		ScoreDampingFunction powerDampingFunction = new PowerDampingFunction();
		return powerDampingFunction.damp(
				linearDampingFunction.damp(
				LogMath.getLogMath().logToLinear(
				(float) wordResult.getConfidence())));
	}
	
	/*
	 * Returns the score for any sentance which includes the word "penis" exactly once.
	 * 
	 * TODO (mdailey): This should be improved to score either for only 1 penis or to take the best
	 * penis. Perhaps a strategy pattern :O
	 */
	public static Optional<Double> getScore(List<WordResult> wordResults) {
		if (getPenisCount(wordResults) == 1) {
			return getScoreForFirstPenis(wordResults);
		} else {
			return Optional.absent();
		}
	}
	
	private static Optional<Double> getScoreForFirstPenis(List<WordResult> wordResults) {
		for (WordResult wordResult : wordResults) {
			if (wordResult.getWord().getSpelling().equalsIgnoreCase("penis")) {
				return Optional.of(scoreFromWordResult(wordResult));
			} 
		}
		return Optional.absent();
	}

	public static int getErrorResultCode(List<WordResult> wordResults) {
		int penisCount = getPenisCount(wordResults);
		if (penisCount == 0) {
			return SayPenisConstants.ERROR_NO_PENIS_DETECTED;
		} else if (penisCount > 1) {
			return SayPenisConstants.ERROR_TOO_MANY_PENISES;
		} else {
			return SayPenisConstants.ERROR_INTERNAL_ERROR;
		}
	}
	
	private static int getPenisCount(List<WordResult> wordResults) {
		int penisesCount = 0;
		for (WordResult wordResult : wordResults) {
			if (wordResult.getWord().getSpelling().equalsIgnoreCase("penis")) {
				penisesCount++;
			}
		}
		return penisesCount;
	}
}
