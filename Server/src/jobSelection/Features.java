package jobSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import Objects.Item;
import Objects.Job;
import Objects.Sendable.SingleTask;
import jobInput.JobProcessor;

/**
 * Features class
 * creates predicates for cancellation calculations
 *
 */
public class Features {

	public static Map<Integer, Predicate<Job>> predicates;

	static {

		predicates = new HashMap<Integer, Predicate<Job>>();

		// Total reward predicate
		predicates.put(TotalReward.ZEROTOFORTY, j -> totalReward(j) >= 0 && totalReward(j) < 40);
		predicates.put(TotalReward.FORTYTOEIGHTY, j -> totalReward(j) >= 40 && totalReward(j) < 80);
		predicates.put(TotalReward.EIGHTYTOONEHUNDREDANDTWENTY, j -> totalReward(j) >= 80 && totalReward(j) < 120);
		predicates.put(TotalReward.ONEHUNDREDANDTWENTYTOONEHUNDREDANDSIXTY,
				j -> totalReward(j) >= 120 && totalReward(j) < 160);
		predicates.put(TotalReward.ONEHUNDREDANDSIXTYPLUS, j -> totalReward(j) >= 160);

		
		// Total weight predicate
		predicates.put(TotalWeight.ZEROTOTEN, j -> totalWeight(j) >= 0 && totalWeight(j) < 10);
		predicates.put(TotalWeight.TENTOTWENTY, j -> totalWeight(j) >= 10 && totalWeight(j) < 20);
		predicates.put(TotalWeight.TWENTYTOTHIRTY, j -> totalWeight(j) >= 20 && totalWeight(j) < 30);
		predicates.put(TotalWeight.THIRTYTOFORTY, j -> totalWeight(j) >= 30 && totalWeight(j) < 40);
		predicates.put(TotalWeight.FORTYPLUS, j -> totalWeight(j) >= 40);

		
		// Highest quantity not included in cancellation learning
		predicates.put(HighestQuantity.ONETOTWO, j -> highestQuantity(j) >= 1 && highestQuantity(j) <= 2);
		predicates.put(HighestQuantity.THREETOFOUR, j -> highestQuantity(j) >= 3 && highestQuantity(j) <= 4);
		predicates.put(HighestQuantity.FIVETOSIX, j -> highestQuantity(j) >= 5 && highestQuantity(j) <= 6);
		predicates.put(HighestQuantity.SEVENPLUS, j -> highestQuantity(j) >= 7);

		// Cancelled predicate
		predicates.put(Cancelled.YES, Job::cancelled);
		predicates.put(Cancelled.NO, j -> !j.cancelled());
	}


	//TOTAL REWARD FEATURE
	public static class TotalReward {
		public static int ZEROTOFORTY = 0;
		public static int FORTYTOEIGHTY = 1;
		public static int EIGHTYTOONEHUNDREDANDTWENTY = 2;
		public static int ONEHUNDREDANDTWENTYTOONEHUNDREDANDSIXTY = 3;
		public static int ONEHUNDREDANDSIXTYPLUS = 4;
	}

	//TOTAL WEIGHT FEATURE
	public static class TotalWeight {
		public static int ZEROTOTEN = 5;
		public static int TENTOTWENTY = 6;
		public static int TWENTYTOTHIRTY = 7;
		public static int THIRTYTOFORTY = 8;
		public static int FORTYPLUS = 9;
	}

	//HIGHEST QUANTITY FEATURE
	public static class HighestQuantity {
		public static int ONETOTWO = 10;
		public static int THREETOFOUR = 11;
		public static int FIVETOSIX = 12;
		public static int SEVENPLUS = 13;

	}

	//CANCELLED FEATURE
	public static class Cancelled {
		public static int YES = 14;
		public static int NO = 15;
	}
	
	public static class ItemCount {	
	}
	
	
	/**
	 * Get the total reward
	 * @param job The job to check
	 * @return The total reward of the job
	 */
	public static double totalReward(Job job) {

		double reward = 0.0;

		for (SingleTask task : job.getTasks()) {

			if (!task.getItemID().equals("dropOff")) {
				reward = reward + JobProcessor.getItem(task.getItemID()).getReward();

			}
		}
		return reward;
	}

	/**
	 * Get the total weight 
	 * @param job The job to check
	 * @return The total weight of the job
	 */
	public static double totalWeight(Job job) {

		double weight = 0.0;

		for (SingleTask task : job.getTasks()) {

			if (!task.getItemID().equals("dropOff")) {
				weight = weight + JobProcessor.getItem(task.getItemID()).getWeight();

			}
		}
		return weight;
	}

	/**
	 * Get the features
	 * @param job The job to check
	 * @return A list of features
	 */
	public static List<Integer> getFeatures(Job job) {

		List<Integer> list = new ArrayList<Integer>();
		Set<Entry<Integer, Predicate<Job>>> entrySet = predicates.entrySet();

		for (Map.Entry<Integer, Predicate<Job>> e : entrySet) {
			Integer f = e.getKey();
			Predicate<Job> p = e.getValue();
			if (p.test(job))
				list.add(f);
		}
		return list;
	}

	/**
	 * Work out the highest quantity of items present in the job
	 * @param job The job to check
	 * @return quantity The highest quantity
	 */
	public static double highestQuantity(Job job) {

		double quantity = 0;

		for (SingleTask task : job.getTasks()) {

			if (task.getQuantity() > quantity) {

				quantity = task.getQuantity();
			}
		}
		return quantity;
	}
}