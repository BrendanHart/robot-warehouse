package jobInput;

import java.util.ArrayList;
import java.util.List;

import Objects.Job;
import Objects.WarehouseMap;

/**
 * Test class
 * View whether jobs are cancelled
 */
public class jobCancelTest {

	public static void main(String[] args) {

		WarehouseMap map = new WarehouseMap("res/drops.csv");

		JobProcessor.processItemFiles("res/items.csv", "res/locations.csv");
		JobProcessor.processJobFiles("res/training_jobs.csv", "res/jobs.csv", "res/cancellations.csv");

		List<Job> jl = new ArrayList<Job>(JobProcessor.getAllJobs().values());
		int count = 0;
		for(Job j : jl) {
			count += j.cancelled() ? 1 : 0;
		}
		
		System.out.println(count);

	}

}
