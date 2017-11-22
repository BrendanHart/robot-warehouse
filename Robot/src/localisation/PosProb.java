package localisation;

/**
 * Used to store the every location and its probability
 * @author Simeon Kostadinov
 *
 */
public class PosProb {

	private int xCoord;
	private int yCoord;
	private float probability;

	/**
	 * Constructor
	 * @param xCoord - the x coordinate of the location 
	 * @param yCoord- the y coordinate of the location 
	 * @param probability - the probability of the location
	 */
	public PosProb(int xCoord, int yCoord, float probability){
		this.setxCoord(xCoord);
		this.setyCoord(yCoord);
		this.setProbability(probability);
	}

	/**
	 * Get the x coordinate
	 * @return The x coordinate
	 */
	public int getxCoord() {
		return xCoord;
	}

	/**
	 * Set the x coordinate
	 * @param xCoord The new x coordinate
	 */
	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	/**
	 * Get the y coordinate
	 * @return The y coordinate
	 */
	public int getyCoord() {
		return yCoord;
	}

	/**
	 * Set the y coordinate
	 * @param xCoord The new y coordinate
	 */
	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	/**
	 * Get the probability
	 * @return The probability
	 */
	public float getProbability() {
		return probability;
	}

	/**
	 * Set the probability
	 * @param probability The new probability
	 */
	public void setProbability(float probability) {
		this.probability = probability;
	}
}