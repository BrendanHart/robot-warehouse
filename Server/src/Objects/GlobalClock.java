package Objects;

/**
 * SHARED OBJECTS
 * Used to represent the clock for keeping track of the robots
 */
public class GlobalClock {
	
	/**
	 * At the start of the program current time is always 0
	 */
	private static int currentTime = 0;
	
	/**
	 * Get the current time
	 * @return The current time (as an int)
	 */
	public static int getCurrentTime() {
		return currentTime;
	}
	
	/**
	 * Increases time by 1
	 */
	public static void increaseTime() {
		currentTime++;
	}
}
