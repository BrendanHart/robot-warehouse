package jobSelection;

import java.util.PriorityQueue;

import Objects.Job;
import jobInput.JobProcessor;

/**
 * Test class
 * View rewards per item
 *
 */
public class TestRewardsCancellation {

    public static void main(String[] args) {

    	JobProcessor.processItemFiles("res/items.csv", "res/locations.csv");
		JobProcessor.processJobFiles("res/training_jobs.csv", "res/jobs.csv", "res/cancellations.csv");
        Probability pCalculator = new Probability(JobProcessor.getAllTrainingJobs().values(), JobProcessor.getAllItems().values());
        for(Job j : JobProcessor.getAllJobs().values()) {
            j.setCancellationProb(pCalculator.probabilityCancelled(j));
        }
        
        PriorityQueue<Job> q = Selection.createQueue();
        while(!q.isEmpty()) {
        	System.out.println(q.remove().getJobID());
        }   
    }
}
