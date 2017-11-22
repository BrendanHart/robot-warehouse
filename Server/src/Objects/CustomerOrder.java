package Objects;

import java.awt.*;
import java.util.List;

import Objects.Sendable.SingleTask;

/**
 * SHARED OBJECTS
 * Used to represent a customer order: the currentJobs, the JobID and the items
 */

public class CustomerOrder {

	public List<Job> currentJobs;
	private String JobID;
	private List<SingleTask> tasks;

	/**
	 * Create a CustomerOrder from just the JobID
	 * @param JobID The ID of the job
	 */
	public CustomerOrder(String JobID) {
		this.JobID = JobID;
	}

	/**
	 * Create a CustomerOrder using the JobID and the tasks to add
	 * @param JobID The ID of the job
	 * @param tasks The tasks for that job
	 */
	public CustomerOrder(String JobID, List<SingleTask> tasks) {
		this.JobID = JobID;
		this.tasks = tasks;
	}

	/**
	 * Add a new task to the list of tasks
	 * @param task Task to add
	 */
	public void addTask(SingleTask task) {
		tasks.add(task);
	}

	/**
	 * Add a new abstract item to the list of SingleTasks
	 * @param item String name of the item to add
	 * @param quantity Quantity to add
	 * @param location Point location to add
	 */
	public void addTask(String item, int quantity, Point location) {
		tasks.add(new SingleTask(item, quantity, location));
	}

	// toString method for debugging purposes
	@Override
	public String toString() {
		return "CustomerOrder [currentJobs=" + currentJobs + ", JobID=" + JobID + ", tasks=" + tasks + "]";
	}

}
