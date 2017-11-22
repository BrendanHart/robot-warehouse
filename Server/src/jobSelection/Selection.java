package jobSelection;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

import Objects.Job;
import jobInput.JobProcessor;
import main.RunServer;
import routePlanning.orderPicks.OrderPicks;

/**
 * Selection class
 * creates and orders priority queue of jobs
 */
public class Selection { 

	/**
	 * Constructor
	 */
	public static PriorityQueue<Job> priorityQueue = new PriorityQueue<Job>(new Comparator<Job>() {

		/**
		 * Overwritten compare method
		 * @param job1 first job
		 * @param job2 second job 
		 */
		@Override
		public int compare(Job job1, Job job2) {

			Integer valueJob1 = (int) ((job1.rewardPerItem() * 100 + job1.rewardPerDistance() * 50) / 2);
			Integer valueJob2 = (int) ((job2.rewardPerItem() * 100 + job2.rewardPerDistance() * 50) / 2);		

			if(job1.getCancellationProb().getProbs()[0] >= 0.5)
				valueJob1 /= 100;
			if(job2.getCancellationProb().getProbs()[0] >= 0.5)
				valueJob2 /= 100;

			return valueJob2.compareTo(valueJob1);
		}
	});


	/**
	 * createQueue method 
	 * @param joblist The initial job list
	 * @return The created priority queue
	 */
	public static PriorityQueue<Job> createQueue(){

		Map<Integer, Job> jobMap = JobProcessor.getAllJobs();
		//OrderPicks op = new OrderPicks(tasks, RunServer.map.getDropoffPoints(), RunServer.map); 
		//add to queue
		for(Job j : jobMap.values())
			if(j.getTotalWeight()<50 && !j.cancelled()){// && j.getCancellationProb().getProbs()[0] < 0.5){
				//System.out.println("job being added to queue: "+ j);
				priorityQueue.add(j);

			}
		return priorityQueue;
	};
}	