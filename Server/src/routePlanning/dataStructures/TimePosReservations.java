package routePlanning.dataStructures;

import java.awt.Point;
import java.util.Vector;

/**
 * A class holding the time position reservations
 */
public class TimePosReservations {

	private Vector<Reservation> reservations = new Vector<Reservation>();
	private int lastReservedTime; //For Optimal WHCA* i.e. checking all reserved time windows ( +1 potentially) but not beyond
	private int firstReservedTime; //Used to deduce if robots are waiting to move i.e. currentTime + 1 < firstReservedTime

	@Override
	public String toString() {
		return reservations.toString();
	}

	/**
	 * Add a reservation 
	 * @param time The time
	 * @param node The node
	 */
	public void addReservation(int time, Point node) {
		reservations.add(new Reservation(time, node));
	}

	/**
	 * Returns true if the given node is reserved at the given time
	 * @param node
	 * @param time
	 * @return
	 */
	public boolean isReserved(Point node, int time){
		for (int i = 0; i < reservations.size(); i++) {
			if (reservations.get(i).getNode().equals(node) && reservations.get(i).getTime() == time) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the node reserved at the given time or null if there is no node reserved at the given time
	 * @param time
	 * @return
	 */
	public Point getReservedNode(int time){
		for (int i = 0; i < reservations.size(); i++) {
			if (reservations.get(i).getTime() == time) {
				return reservations.get(i).getNode();
			}
		}
		return null;
	}

	/**
	 * Set the first reserved time
	 * @param firstReservedTime The time to set
	 */
	public void setFirstReservedTime(int firstReservedTime) {
		this.firstReservedTime = firstReservedTime;
	}

	/**
	 * Set the greatest reserved time
	 * @param lastReservedTime The time to set
	 */
	public void setGreatestReservedTime(int lastReservedTime) {
		this.lastReservedTime = lastReservedTime;
	}

	/**
	 * Get the first reserved time
	 * @return The first reserved time
	 */
	public int getFirstReservedTime() {
		return firstReservedTime;
	}

	/**
	 * Get the last reserved time
	 * @return The last reserved time
	 */
	public int getLastReservedTime() {
		return lastReservedTime;
	}
}