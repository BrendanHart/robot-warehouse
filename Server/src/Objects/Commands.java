package Objects;

/**
 * SHARED OBJECTS
 * Used to represent a single commands
 */
public class Commands {

	private String robot;
	private Object command;

	/**
	 * Constructor
	 * @param robot The specific robot
	 * @param command The command to send
	 */
	public Commands(String robot, Object command) {
		this.robot = robot;
		this.command = command;
	}

	/**
	 * Get the robot
	 * @return The robot name
	 */
	public String getRobot() {
		return robot;
	}

	/**
	 * Get the command
	 * @return The command
	 */
	public Object getCommand() {
		return command;
	}
}
