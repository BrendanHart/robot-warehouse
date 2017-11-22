package Objects.Sendable;

/**
 * SHARED OBJECTS
 * Imitates a point so that it can be sent over the network, represents the point for items to be dropped off
 */
public class DropOffPoint implements SendableObject {
	private int x;
	private int y;
	
	public DropOffPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Get the X coordinate
	 * @return The X coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get the X coordinate
	 * @return The Y coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Set the X coordinate
	 * @param x The X coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Set the Y coordinate
	 * @param x The Y coordinate
	 */public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Gets the parameters in csv format
	 * @return all parameters, seperated by commas
	 */
	public String parameters() {
		return ("DropOffPoint," + x + "," +  y);
	}
	
	public String toString() {
		return "DropOffPoint [x=" + x + ", y=" + y + "]";
	}
}