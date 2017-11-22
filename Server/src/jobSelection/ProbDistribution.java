package jobSelection;

import java.util.Collection;
import java.util.function.Predicate;

import jobInput.JobProcessor;

/**
 * Prob distribution class
 * calculates the probability distribution using predicates
 */
public class ProbDistribution {

	private float[] probabilities;

	/**
	 * Constructor
	 * @param probabilities array of probabilities
	 */
	public ProbDistribution(float[] probabilities) {
		this.probabilities = probabilities;
	}

	/**
	 * Check if normalised
	 * @return A boolean value of if the probs add up to 1
	 */
	public boolean isNormalised() {
		return (sumOfProbs() == 1);
	}

	/**
	 * Normalise method
	 */
	public void normalise() {
		float multiplier = 1.0f / sumOfProbs();
		for(int i = 0; i < probabilities.length; i++) {
			probabilities[i] *= multiplier;
		}
	}

	/**
	 * Get the probs
	 * @return The array of probs
	 */
	public float[] getProbs() {
		return probabilities;
	}

	/**
	 * Get the sum of probs
	 * @return Sum of probs
	 */
	private float sumOfProbs() {
		float sum = 0.0f;
		for(int i = 0; i < probabilities.length; i++) {
			sum += probabilities[i]; 
		}
		return sum;
	}

	/**
	 * Calculate the probability distribution
	 * @param col collection 
	 * @param p predicate
	 * @return probability distribution
	 */
	public static <T> ProbDistribution calculateProbDistr(Collection<T> col, Predicate<T> p) {

		float[] prob = new float[2];
		int numSatisfyingP = (int) col
				.stream()
				.filter(p)
				.count();
		prob[0] = (float)numSatisfyingP / (float)JobProcessor.getAllJobs().size();
		prob[1] = 1 - prob[0];

		return new ProbDistribution(prob);

	}

	/**
	 * toString method
	 * @return s string of probabilities
	 */
	// For debugging purposes.
	@Override
	public String toString() {
		String s = "[";
		for(int i = 0; i < probabilities.length; i++) {
			if(i == probabilities.length - 1) {
				s += probabilities[i] + "]";
			} else {
				s += probabilities[i] + ",";
			}
		}
		return s;
	}

}
