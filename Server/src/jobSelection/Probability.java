package jobSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Objects.Item;
import Objects.Job;
import Objects.Sendable.SingleTask;

/**
 * Probability class 
 * Used to work out the chance/likelyhood of a job being cancelled
 */
public class Probability {

	private int countCancelled = 0;
    public double[] probs;
    public double[][] conProbs;
    public Map<String, HashMap<Integer, Integer>> itemCancelledCount;
    public Map<String, HashMap<Integer, Integer>> itemNotCancelledCount;
    private List<Job> trainingSet;
    private List<Item> items;
    
    /**
     * Constructor
     * @param trainingSet collection of jobs
     * @param items collection of items
     */
    public Probability(Collection<Job> trainingSet, Collection<Item> items) {

    	this.trainingSet = new ArrayList<Job>(trainingSet);
        this.items = new ArrayList<Item>(items);
    	probs = new double[Features.predicates.size()];
        conProbs = new double[Features.predicates.size()][Features.predicates.size()];
        itemCancelledCount = new HashMap<String, HashMap<Integer, Integer>>();
    	itemNotCancelledCount = new HashMap<String, HashMap<Integer, Integer>>();
        
    	countCancelled = (int) trainingSet.stream().filter(Job::cancelled).count();
    	
        for(Item item : items) {
        	for(int i = 0; i < 4; i++) {
        		HashMap<Integer, Integer> map;
        		if(itemCancelledCount.containsKey(item.getItemID()))
        			map = new HashMap<Integer, Integer>(itemCancelledCount.get(item.getItemID()));
        		else
        			map = new HashMap<Integer, Integer>();
        		Integer count = countOccurencesOfGroupAndItem(item, i, true);
        		map.put(i, count);
        		itemCancelledCount.put(item.getItemID(), map);
        		
        		count = countOccurencesOfGroupAndItem(item, i, false);
        		if(itemNotCancelledCount.containsKey(item.getItemID()))
        			map = new HashMap<Integer, Integer>(itemNotCancelledCount.get(item.getItemID()));
        		else
        			map = new HashMap<Integer, Integer>();
        		map.put(i,  count);
        		itemNotCancelledCount.put(item.getItemID(), map);
        	}
        }
        populateProbs();
    }

    /**
     * Count the occurances of group and item
     * @param item
     * @param i
     * @param cancelled is the job a cancelled job?
     * @return count occurrences of that quantity and job combo
     */
    private Integer countOccurencesOfGroupAndItem(Item item, int i, boolean cancelled) {
		
    	int count = 0;
    	
    	for(Job j : trainingSet) {
    		
    		if(j.cancelled() == cancelled) {
    			
				for(SingleTask t : j.getTasks()) {
					
					if( i == 3) {
						if(t.getItemID().equals(item.getItemID()) && (i+1) <= t.getQuantity()) {
							count++;
						}
					} else {
						if(t.getItemID().equals(item.getItemID()) && (i+1) == t.getQuantity()) {
							count++;
						}
					}	
				}
    		}
    	}
    	
    	return count;
    	
	}

    /**
     * Populate probability arrays
     */
	public void populateProbs() {
        
        for(int i = 0; i < probs.length; i++) {
            
            probs[i] = (float)trainingSet.stream()
                                  .filter(Features.predicates.get(i))
                                  .count() / (float)trainingSet.size();
      
            for(int j = 0; j < probs.length; j++) {
                conProbs[i][j] = (float)trainingSet.stream()
                                    .filter(Features.predicates.get(i))
                                    .filter(Features.predicates.get(j))
                                    .count() /
                              (float)trainingSet.stream()
                                    .filter(Features.predicates.get(j))
                                    .count();
         
            }
        }
    }

	/**
	 * Get features
	 * @param j the job
	 * @return The list of features(integers)
	 */
    public List<Integer> getFeatures(Job j) {

        List<Integer> featuresOfJob = new ArrayList<Integer>();

        for(int i = 0; i < Features.predicates.size(); i++) {
            if(i != Features.Cancelled.NO && i != Features.Cancelled.YES && Features.predicates.get(i).test(j))
                featuresOfJob.add(i);
        }

        return featuresOfJob;

    }
    
    /**
     * Probability of cancelled method
     * @param j the job
     * @return The probability distribution for that job 
     */
    public ProbDistribution probabilityCancelled(Job j) {
    	
    	double prob = 0d;
    	double probNot = 0d;
    	
    	List<Integer> features = getFeatures(j);
    	Map<String, Integer> itemFeatures = getItemFeatures(j);
    	
    	//probability of cancellation given those features
    	double pGivenFeatures = 1d;
    	for(Integer f : features) {
    		double p = conProbs[f][Features.Cancelled.YES];
    		pGivenFeatures *= (p == 0.0d ? 0.001d : p);
    	}
    
    	for(Map.Entry<String, Integer> e : itemFeatures.entrySet()) {
    		int i = 0;
    		if(e.getValue() < 4)
    			i = e.getValue() - 1;
    		else
    			i = 3;

    		double p = (float) itemCancelledCount.get(e.getKey()).get(i) / (float)countCancelled;
    		pGivenFeatures *= (p == 0.0d ? 0.001d : p);
    	}
    	
    	prob = pGivenFeatures * probs[Features.Cancelled.YES];
    	
    	// Probaility of not being cancelled given those features
    	pGivenFeatures = 1d;
    	for(Integer f : features) {
    		double p = conProbs[f][Features.Cancelled.NO];
    		pGivenFeatures *= (p == 0.0d ? 0.001d : p);
    	}
    	
    	for(Map.Entry<String, Integer> e : itemFeatures.entrySet()) {
    		int i = 0;
    		if(e.getValue() < 4)
    			i = e.getValue() - 1;
    		else
    			i = 3;

    		double p = (float) itemNotCancelledCount.get(e.getKey()).get(i) / (float)(trainingSet.size() - countCancelled);
    		pGivenFeatures *= (p == 0.0d ? 0.001d : p);
    	}
    	
    	probNot = (pGivenFeatures) * probs[Features.Cancelled.NO];
    	
    	// Return the probability distribution
    	ProbDistribution p = new ProbDistribution(new float[]{ (float) prob, (float) probNot });
    	p.normalise();
    	return p;
    	
    }
    
    /**
     * Get item features method
     * @param j the job
     * @return map item features
     */
    public Map<String, Integer> getItemFeatures(Job j) {
    	Map<String, Integer> map = new HashMap<String, Integer>();
    	for(SingleTask t : j.getTasks()) {
    		map.put(t.getItemID(), t.getQuantity());
    	}
    	return map;
    }

}
