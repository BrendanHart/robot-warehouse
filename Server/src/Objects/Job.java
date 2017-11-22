package Objects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//import JobInput.JobProcessor;
import Objects.Sendable.SingleTask;
import jobInput.JobProcessor;
import jobSelection.ProbDistribution;
import main.RunServer;
import routePlanning.orderPicks.OrderPicks;

/**
 * SHARED OBJECTS
 * Represents a Job to be carried out
 */
public class Job {

	private List<SingleTask> tasks;
	private boolean cancelled;
	private boolean ordered;
	private ProbDistribution cancellationProb;
	private Map<String, Item> items;
	private int jobid;
	private int distanceToTravel;
	public Point dropOff;
	public double totalReward;

	/**
	 * Create an empty job
	 * @param jobid The ID of the job
	 * @param items The items to add to the job
	 */
	public Job(int jobid) {
		this.tasks = new ArrayList<>();
		this.cancelled = false;
		this.ordered = false;
		this.cancellationProb = new ProbDistribution(new float[]{0.0f, 1.0f});
		this.jobid = jobid;
	}

	/**
	 * Create a job with items
	 * @param jobid The ID of the job
	 * @param tasks The tasks in the job
	 * @param items The items for the job
	 */
	public Job(int jobid, List<SingleTask> tasks) {
		this.tasks = tasks;
		this.cancelled = false;
		this.ordered = false;
		this.cancellationProb = new ProbDistribution(new float[]{0.0f, 1.0f});
		this.jobid = jobid;
	}

	/**
	 * Get the ID of the job
	 * @return The ID of the job
	 */
	public int getJobID(){
		return jobid;
	}

	/**
	 * Add a task to the list of tasks
	 * @param t The task to be added
	 */
	public void addTask(SingleTask t) {
		tasks.add(t);
	}

	/**
	 * Get the list of tasks for the current job
	 * @return The list of tasks
	 */
	public List<SingleTask> getTasks() {
		return tasks;
	}

	/**
	 * Add an item to the list of job items
	 * @param itemID The item id to be added
	 * @param qty The amount of the item needed
	 */
	public void addTask(String itemID, int qty, Point location) {
		tasks.add(new SingleTask(itemID, qty, location));
	}

	/**
	 * Get the task at the given index
	 * @param i The given index
	 * @return The task at the given index if it exists
	 */
	public Optional<SingleTask> getTaskAtIndex(int i) {
		if(i >= 0 && i < tasks.size())
			return Optional.of(tasks.get(i));
		else
			return Optional.empty();
	}

	/**
	 * Get the task with the given item ID
	 * @param itemID The item ID
	 * @return The task with the given item ID if it exists
	 */
	public Optional<SingleTask> getTask(String itemID) {
		for(SingleTask task : tasks)
			if(task.getItemID().equals(itemID))
				return Optional.of(task);
		return Optional.empty();
	}

	/**
	 * Set the cancellation probability
	 * @param probDistribution The cancellation probability
	 */
	public void setCancellationProb(ProbDistribution probDistribution) {
		this.cancellationProb = probDistribution;
	}

	/**
	 * Get the cancellation probability
	 * @return The cancellation probability
	 */
	public ProbDistribution getCancellationProb() {
		return cancellationProb;
	}

	/**
	 * Check if this job is cancelled
	 * @return If this job is cancelled
	 */
	public boolean cancelled() {
		return cancelled;
	}

	/**
	 * Cancel this job
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * Get the number of tasks
	 * @return The number of tasks
	 */
	public int getNumOfTasks() {
		int count = 0;
		for(int i = 0; i < tasks.size(); i++) {
			count++;
		}
		return count; 
	}

	/**
	 * Calculate the reward for this job per item
	 * @return The reward per item
	 */
	public double rewardPerItem() {

		int numOfItems = 0;
		double reward = 0f;

		for(SingleTask task : tasks) {
			if(!task.getItemID().equals("dropOff")) {
				//Item item = items.get(task.getItemID());
				Item item = JobProcessor.getItem(task.getItemID());
				numOfItems += task.getQuantity();
				reward += item.getReward() * task.getQuantity();
			}
		}
		return (reward / (double) numOfItems);
	}

	/**
	 * Get the total reward
	 * @return The total reward
	 */
	public double getTotalReward() {

		double reward = 0.0;

		for(SingleTask task : tasks) {
			if(!task.getItemID().equals("dropOff")) {
				//Item item = items.get(task.getItemID());
				Item item = JobProcessor.getItem(task.getItemID());
				reward += item.getReward() * task.getQuantity();
			}
		}
		return reward;
	}

	/**
	 * Calculate the reward for this job per weight
	 * @return The reward per weight
	 */
	public double rewardPerWeight() {

		double reward = 0f;
		double weight = 0f;

		for(SingleTask task : tasks) {
			if(!task.getItemID().equals("dropOff")) {
				Item item = JobProcessor.getItem(task.getItemID());
				reward += item.getReward() * task.getQuantity();
				weight += item.getWeight() * task.getQuantity();
			}
		}

		return (reward / weight);
	}

	/**
	 * Calculate the total weight of a job
	 * @return The total weight of the job
	 */
	public double getTotalWeight() {

		double totalweight = 0.0;

		for(SingleTask task : tasks) {
			if(!task.getItemID().equals("dropOff")) {
				Item item = JobProcessor.getItem(task.getItemID());
				totalweight = totalweight + (item.getWeight() * task.getQuantity());
			}
		}
		return totalweight;
	}

	/**
	 * Get the reward per distance
	 * @return A double of the reward per distance
	 */
	public double rewardPerDistance() {
		if(ordered)
			return getTotalReward() / distanceToTravel;
		else {
			OrderPicks op = new OrderPicks(tasks, RunServer.map.getDropoffPoints(), RunServer.map); 
			this.dropOff=op.dropOff;
			this.tasks = new ArrayList<SingleTask>();
			for(SingleTask task : op.orderedItems){
				this.tasks.add(task);
			}
			distanceToTravel = op.getFinalDistance();
			this.ordered = true;
			return getTotalReward() / distanceToTravel;
		}	
	}

	// toString method for debugging purposes
	@Override
	public String toString() {
		return "Job [tasks=" + tasks + ", cancelled=" + cancelled + ", ordered=" + ordered + ", cancellationProb="
				+ cancellationProb + ", jobid=" + jobid + ", distanceToTravel=" + distanceToTravel
				+ "]";
	}   	
}
