package Objects;

import java.util.ArrayList;

import Objects.Sendable.RobotInfo;

/**
 * SHARED OBJECTS
 * Used to hold the robots connected
 */
public class AllRobots {

	private static ArrayList<RobotInfo> robots = new ArrayList<RobotInfo>();

	/**
	 * Get all the robots connected
	 * @return A ArrayList of all robots
	 */
	public synchronized static ArrayList<RobotInfo> getAllRobots(){
		return robots;
	}

	/**
	 * Get a specific robot for a certain name, or null if it doesn't exist
	 * @param name The name of the robot to get
	 * @return The RobotInfo for the named robot
	 */
	public synchronized static RobotInfo getRobot(String name){
		for(RobotInfo robot : robots){
			if(robot.getName().equals(name)) return robot;
		}
		return null;
	}

	/**
	 * Add a robot to the ist
	 * @param newBot The RobotInfo of the new robot to add
	 */
	public synchronized static void addRobot(RobotInfo newBot){
		robots.add(newBot);
	}

	/**
	 * Check a specified robot exists
	 * @param name The name of the robot to check
	 * @return A boolean value of whether the named robot exists
	 */
	public synchronized static boolean checkExists(String name){
		for(RobotInfo robot : robots){
			if(robot.getName().equals(name)) return true;
		}
		return false;
	}

	/**
	 * Modify an existing robot
	 * @param name The name of the robot to modify
	 * @param newInfo The new RobotInfo object for the robot to modify
	 */
	public synchronized static void modifyRobot(String name, RobotInfo newInfo) {
		ArrayList<RobotInfo> newList = new ArrayList<RobotInfo>();
		boolean robotFound = false;

		for(RobotInfo robot : robots) {
			if(robot.getName().equals(name)) {
				newList.add(newInfo);
				robotFound = true;
			}
			else{
				newList.add(robot);
			}
		}

		robots = newList;
		if(!robotFound) throw new IllegalArgumentException("Robot " + name + " not found");
	}

	/**
	 * Remove an added robot
	 * @param name The robot to remove
	 */
	public synchronized static void removeRobot(String name) {
		ArrayList<RobotInfo> newList = new ArrayList<RobotInfo>();
		boolean robotFound = false;

		for(RobotInfo robot : robots) {
			if(robot.getName().equals(name)) {
				robotFound = true;
			}
			else {
				newList.add(robot);
			}
		}

		robots = newList;
		if(!robotFound) throw new IllegalArgumentException("Robot " + name + " not found");
	}
}
