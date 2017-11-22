package Objects;

import java.awt.Point;

/**
 * SHARED OBJECTS
 * Used to represent a single item: It's location, weight and reward.
 */
public class Item {

	private final Point location;
	private final double reward;
	private final double weight;

	/**
	 * Create a new item
	 * @param location the coordinates of the item pick up
	 * @param reward The reward for this item
	 * @param weight The weight for this item
	 */
	public Item(Point location, double reward, double weight) {
		this.location = location;
		this.reward = reward;
		this.weight = weight;
	}

	/**
	 * Get the location
	 * @return Point the location
	 */
	public Point getLocation(){
		return location;
	}

	/**
	 * Get reward of the item
	 * @return The reward
	 */
	public double getReward() {
		return reward;
	}

	/**
	 * Get weight of the item
	 * @return The weight
	 */
	public double getWeight() {
		return weight;
	}

	// toString method for debugging purposes
	@Override
	public String toString() {
		return "Item [location=" + location + ", reward=" + reward + ", weight=" + weight + "]";
	}

}
