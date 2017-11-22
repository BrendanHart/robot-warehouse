package jobInput;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import Objects.Item;
import Objects.Job;
import Objects.WarehouseMap;
import Objects.Sendable.SingleTask;
import main.RunServer;
import routePlanning.orderPicks.OrderPicks;

/**
 * Class that carries out all the necessary processing and stores the data.
 */
public class JobProcessor {

	private static Map<Integer, Job> trainingData = new HashMap<Integer, Job>();
	private static Map<Integer, Job> jobs = new HashMap<Integer, Job>();
	private static Map<String, Item> items = new HashMap<String, Item>();

	/**
	 * Get an item from its ID.
	 * @param id The item id.
	 * @return The item.
	 */
	public static Item getItem(String id) {
		return items.get(id);
	}

	/**
	 * Get a job from its ID.
	 * @param id The job id.
	 * @return The job.
	 */
	public static Job getJob(int id) {
		return jobs.get(id);
	}

	/**
	 * Get all the jobs.
	 * @return All the jobs.
	 */
	public static Map<Integer, Job> getAllTrainingJobs() {
		return trainingData;
	}

	/**
	 * Get all the jobs.
	 * @return All the jobs.
	 */
	public static Map<Integer, Job> getAllJobs() {
		return jobs;
	}

	/**
	 * Get all the itemsthis.items = items;.
	 * @return All the items.
	 */
	public static Map<String, Item> getAllItems() {
		return items;
	}

	/**
	 * Read a file into a list of lines.
	 * @param fileName The file to be read.
	 * @return The contents of the file as a list of strings.
	 */
	public static Optional<List<String>> readFile(String fileName) {
		try {
			List<String> fileContents = new ArrayList<String>();

			FileReader fr = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fr);

			String line;
			while((line = reader.readLine()) != null) {
				fileContents.add(line);
			}

			fr.close();
			reader.close();

			return Optional.of(fileContents);
		} catch (FileNotFoundException e) { //catch empty file
			return Optional.empty();
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	/**
	 * Process the job files.
	 * @param trainingFile The training file.
	 * @param jobFile The job file.
	 * @param cancelFile The cancellation file.
	 */
	public static void processJobFiles(String trainingFile, String jobFile, String cancelFile) {

		//contents of training file
		Optional<List<String>> trainingContents = readFile(trainingFile);
		//contents of jobs file
		Optional<List<String>> jobsContents = readFile(jobFile);
		//contents of cancellation file
		Optional<List<String>> cancellationsContents = readFile(cancelFile);

		//nothing in files? error
		if(!trainingContents.isPresent()) {
			System.err.println("Error processing job file.");
			return;
		}

		if(!jobsContents.isPresent()) {
			System.err.println("Error processing job file.");
			return;
		}

		if(!cancellationsContents.isPresent()) {
			System.err.println("Error processing cancellations file.");
			return;
		}

		for(String jobStr : trainingContents.get()) {
			// Each data element is separated by a comma.
			String[] jobArr = jobStr.split(",");

			// Create a new job.
			Job newJob = new Job(Integer.parseInt(jobArr[0]));;


			// Add each the item and the quantity of the item to the job.
			for(int i = 1; i < jobArr.length; i+=2) {
				newJob.addTask(jobArr[i], Integer.parseInt(jobArr[i+1]), items.get(jobArr[i]).getLocation());
			}
			//System.out.println(newJob.getTasks());
			//System.out.println()
			//OrderPicks op = new OrderPicks(newJob.getTasks(), RunServer.map.getDropoffPoints(), RunServer.map);


			trainingData.put(Integer.parseInt(jobArr[0]), newJob);
			// Add a job to the list.
			//jobs.put(Integer.parseInt(jobArr[0]), new Job(Integer.parseInt(jobArr[0]), op.orderedItems, items));
		}

		for(String jobStr : jobsContents.get()) {
			// Each data element is separated by a comma.
			String[] jobArr = jobStr.split(",");

			// Create a new job.
			Job newJob = new Job(Integer.parseInt(jobArr[0]));;


			// Add each the item and the quantity of the item to the job.
			for(int i = 1; i < jobArr.length; i+=2) {
				newJob.addTask(jobArr[i], Integer.parseInt(jobArr[i+1]), items.get(jobArr[i]).getLocation());
			}
			//System.out.println(newJob.getTasks());
			//System.out.println()
			//OrderPicks op = new OrderPicks(newJob.getTasks(), RunServer.map.getDropoffPoints(), RunServer.map);


			jobs.put(Integer.parseInt(jobArr[0]), newJob);
			// Add a job to the list.
			//jobs.put(Integer.parseInt(jobArr[0]), new Job(Integer.parseInt(jobArr[0]), op.orderedItems, items));
		}

		for(String cancelStr : cancellationsContents.get()) {
			String[] cancelArr = cancelStr.split(",");
			if(cancelArr[1].equals("1"))
				trainingData.get(Integer.parseInt(cancelArr[0])).cancel();
		}


	}

	/**
	 * Process the item files.
	 * @param itemFile The item file.
	 * @param locFile The location file.
	 */
	public static void processItemFiles(String itemFile, String locFile) {

		Optional<List<String>> itemsContents = readFile(itemFile);
		Optional<List<String>> locationsContents = readFile(locFile);

		if(!itemsContents.isPresent()) {
			System.err.println("Error processing items file.");
			return;
		}

		if(!locationsContents.isPresent()) {
			System.err.println("Error processing locations file");
			return;
		}


		for(String itemStr : itemsContents.get()) {

			// Each data element is separated by a comma.
			String[] itemArr = itemStr.split(",");

			for(String locationStr : locationsContents.get()){

				String[] locationArr = locationStr.split(",");

				if(itemArr[0].equals(locationArr[2])){

					// Create a new item
					Item newItem = new Item(itemArr[0],
							new Point(
									Integer.parseInt(locationArr[0]),
									Integer.parseInt(locationArr[1])),
							Double.parseDouble(itemArr[1]),
							Double.parseDouble(itemArr[2]));



					// Add an item object to the list.
					items.put(itemArr[0], newItem);
					break;
				}
			}
		}
	}

}
