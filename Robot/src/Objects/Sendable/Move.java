package Objects.Sendable;

import java.awt.Point;

/**
 * SHARED OBJECTS
 * Represents a single move for the Robot to execute
 */
public class Move implements SendableObject {

	private char direction;
	private Point nextLocation;

	public Move(char direction, Point nextLocation) {
		this.direction = direction;
		this.nextLocation = nextLocation;
	}

	/**
	 * Get the direction
	 * @return The direction
	 */public char getDirection() {
		 return direction;
	 }

	 /**
	  * Get the next location
	  * @return The next location
	  */
	 public Point getNextLocation() {
		 return nextLocation;
	 }

	 // toString method for debugging purposes
	 @Override
	 public String toString() {
		 return "Move [direction=" + direction + ", nextLocation=" + nextLocation + "]";
	 }


	 /**
	  * Gets the parameters in csv format
	  * @return all parameters, seperated by commas
	  */
	 public String parameters() {
		 return ("Move," + direction+","+(int)nextLocation.getX() + "," + (int)nextLocation.getY());
	 }

}
