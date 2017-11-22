package Objects.Sendable;

/**
 * SHARED OBJECTS
 * Used to initialise information about the robot when the server starts, after location information is passed
 */
public class StartUpItem implements SendableObject {

	private String name;
	private int x;
	private int y;
	private char direction; // N, E, S, W

	public StartUpItem(String name, int x, int y, char direction) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	/**
	 * Get the name of the robot
	 * @return Name of the robot
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get X coordinate of the robot
	 * @return X coordinate of the robot
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Get Y coordinate of the robot
	 * @return Y coordinate of the robot
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Get the direction that the robot is facing
	 * @return Direction the robot is facing (enum)
	 */
	public char getDirection() {
		return this.direction;
	}

	// toString method for debugging purposes
	public String toString() {
		return "StartUpItem [name=" + name + ", x=" + x + ", y=" + y + ", direction=" + direction + "]";
	}

	/**
	 * Gets the parameters in csv format
	 * @return all parameters, seperated by commas
	 */
	public String parameters() {
		return ("StartUpItem," + name + "," + x + "," + y + "," + direction);
	}
}