package routePlanning.dataStructures;

import java.awt.Point;
import java.util.Vector;

import Objects.Sendable.RobotInfo;

/**
 * A class holding the time position reservations
 */
public class RobotsReservations {
	private static Vector<RobotInfo> robots = new Vector<RobotInfo>(0);
	
	/**
	 * Add a robot
	 * @param robot The RobotInfo of the robot to add
	 */
	public static void AddRobot(RobotInfo robot){
		robots.add(robot);
	}
	
	/**
	 * Returns whether any robot has reserved the given node
	 * @param node The node to check
	 * @param time The time to check at
	 * @return A boolean of whether any robot has reserved the given node
	 */
	public static boolean IsReserved(Point node, int time){
		for (int i = 0; i < robots.size(); i++){
			if (robots.get(i).IsReserved(node, time))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns whether any robot EXCEPT the given one has reserved the given node
	 * @param robot The robot to exclude
	 * @param node The node to check 
	 * @param time The time to check at
	 * @return A boolean of whether any robot except the one passed has resreved the given node
	 */
	public static boolean IsReserved(RobotInfo robot, Point node, int time){
		for (int i = 0; i < robots.size(); i++){
			if (!robot.equals(robots.get(i)) && robots.get(i).IsReserved(node, time))
				return true;
		}
		return false;
	}
	
	/**
	 * Check if reserved for stopping 
	 * @param node The node to check
	 * @param time The time to check at
	 * @return A boolean value of whether the node is reserved for stopping
	 */
	public static boolean isReservedForStopping(Point node, int time){
		for (int i = 0; i < robots.size(); i++){
			if (robots.get(i).isReservedForStopping(node, time))
				return true;
		}
		return false;
	}
	
	/**
	 * Get the greatest reserved time
	 * @return The greatest reserved time
	 */
	public static int getGreatestReservedTime(){
		int largestReservedTime = 0;
		int tempLastReservedTime = 0;
		for (int i = 0; i < robots.size(); i++){
			tempLastReservedTime = robots.get(i).getLastReservedTime();
			if (largestReservedTime < tempLastReservedTime)
				largestReservedTime = tempLastReservedTime;
		}
		return largestReservedTime;
	}
}
