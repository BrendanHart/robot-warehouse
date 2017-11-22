package routePlanning.dataStructures;

import java.awt.Point;

/**
 * Handles reservations
 */
public class Reservation {
	private int time;
	private Point node;

	@Override
	public String toString()
	{
		String s="reservation at "+node+" for time "+time+"\n"; 
		return s;
	}

	public Reservation(int time, Point node) {
		this.time = time;
		this.node = node;
	}

	/** 
	 * Get the time of the reservation
	 * @return The time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Get the node of the reservation
	 * @return The node
	 */
	public Point getNode() {
		return node;
	}
}
