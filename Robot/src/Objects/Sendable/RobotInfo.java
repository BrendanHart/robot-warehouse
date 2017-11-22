package Objects.Sendable;

import java.awt.Point;

import Objects.Direction;

/**
 * SHARED OBJECTS Used to represent information about a single robot
 */
public class RobotInfo implements SendableObject {

	private String name;
	private Point location;
	private Direction direction;

	/**
	 * Create a new RobotInfo object with just the name
	 * @param name The name of the robot
	 */
	public RobotInfo(String name) {
		this.name = name;
	}

	/**
	 * Create a new RobotInfo object with more information
	 * @param name The name of the robot
	 * @param location The current location of the robot
	 * @param direction The current direction the robot is facing
	 */
	public RobotInfo(String name, Point location, Direction direction) {
		this.name = name;
		this.location = location;
		this.direction = direction;
	}

	/**
	 * Get the name of the robot
	 * @return The name of the robot
	 */
	public String getName() {
		return name;
	}

	@Override
	public String parameters() {
		return "RobotInfo," + name + "," + (int)location.getX() + "," + (int)location.getY() + "," + direction;
	}
}