package Objects.Sendable;

/**
 * SHARED OBJECTS
 * A report sent when a job has been completed
 */
public class CompleteReport implements SendableObject {

	private Boolean isPickup;
	private Boolean wasCompleted;
	private Boolean wasCancelled;

	public CompleteReport(Boolean isPickup, Boolean wasCompleted, Boolean wasCancelled) {
		this.isPickup = isPickup;
		this.wasCompleted = wasCompleted;
		this.wasCancelled = wasCancelled;
	}

	/**
	 * Get if an item has been picked up
	 * @return A boolean value of whether an item has been picked up
	 */
	public Boolean getIsPickup() {
		return isPickup;
	}

	/**
	 * Get if a task has been completed
	 * @return A boolean value of whether a task has been completed
	 */
	public Boolean wasCompleted() {
		return wasCompleted;
	}
	
	/**
	 * Get if the task was cancelled
	 * @return A boolean value of whether a task has been cancelled
	 */
	public Boolean wasCancelled() {
		return wasCancelled;
	}

	// toString method for debugging purposes
	@Override
	public String toString() {
		return "CompleteReport [isPickup=" + isPickup + ", wasCompleted=" + wasCompleted + ", wasCancelled=" + wasCancelled + "]";
	}

	/**
	 * Gets the parameters in csv format
	 * @return all parameters, seperated by commas
	 */
	public String parameters() {
		return ("CompleteReport,"+isPickup+","+wasCompleted+","+wasCancelled);
	}
}