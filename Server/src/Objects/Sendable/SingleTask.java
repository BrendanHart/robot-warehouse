package Objects.Sendable;

import java.awt.Point;

/**
 * SHARED OBJECTS
 * Used to represent a single task: what item is needed and how many are needed.
 */
public class SingleTask implements SendableObject {

	private String itemID;
	private int quantity;
	private Point location;

	/**
	 * Create a new single task
	 * @param itemID The item ID
	 * @param quantity The quantity of the item
	 */
	public SingleTask(String itemID, int quantity, Point location) {

		this.itemID = itemID;
		this.quantity = quantity;
		this.location = location;

	}

	/**
	 * Get the item ID
	 * @return The item ID
	 */
	public String getItemID() {
		return itemID;
	}

	/**
	 * Get the quantity
	 * @return The quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Get the location
	 * @return The location
	 */
	public Point getLocation() {
		return location;
	}

	// toString method for debugging purposes
	@Override
	public String toString() {
		return "SingleTask [item=" + itemID + ", quantity=" + quantity + ", location=" + location + "]";
	}

	/**
	 * Gets the parameters in csv format.
	 * @return all parameters, seperated by commas.
	 */
	public String parameters() {
		return ("SingleTask," + itemID + "," + quantity + "," + (int)location.getX() + "," + (int)location.getY());
	}

}