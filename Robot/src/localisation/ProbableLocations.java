package localisation;

import java.util.ArrayList;

import rp.robotics.mapping.GridMap;

/**
 * Calculates the possible locations
 * @author Simeon Kostadinov
 *
 */
public class ProbableLocations {

	// Initialize fields
	private GridMap map; // the map
	private float coordRange; // the range got form the map
	private float cellSize; // the size of the cell
	private float beginningRange; // first range (smaller)
	private float appropriateRange; // second range (medium)
	private float doubleCellRange; // third range (highest)

	private final float cameraWheelsDistance = 2.0f; // the distance between the camera and the wheels

	// Probabilities calculated after gathering test data
	private final float zeroProbability = 0; 
	private final float fullProbability = 1;
	private final float probabilitySmall = 0.20f;
	private final float probabilityBig = 0.80f;

	// Array to store the locations
	private ArrayList<PosProb> locs = new ArrayList<PosProb>();
	// helper array
	private ArrayList<PosProb> newLocs = new ArrayList<PosProb>();

	/**
	 * cellSize
	 * beginningRange - the cell size
	 * appropriateRange - threshold decided after testing data
	 * doubleCellSize - twice the cell size
	 * fill the array with all possible locations
	 * @param map
	 */
	public ProbableLocations(GridMap map){
		this.map=map;
		this.cellSize = map.getCellSize()*100;
		this.beginningRange = cellSize;
		this.appropriateRange = 5*cellSize/3;
		this.doubleCellRange = 2*cellSize;


		for(int i=0;i<=map.getXSize();i++){

			for(int j=0;j<=map.getYSize();j++){
				if(!map.isObstructed(i, j)){
					locs.add(new PosProb(i, j, (float)1/initialSize()));				
				}
			}
		}
	}

	/**
	 * Decides the probability of all possible locations from the range sensor data
	 * @param heading - where the robot is facing
	 * @param range - the range that the sensor gives
	 */
	public void setLocations(float heading, float range){
		//range = range + cameraWheelsDistance;

		for(int p = 0; p < locs.size(); p++){
			int i = (int)locs.get(p).getxCoord();
			int j = (int)locs.get(p).getyCoord();
			coordRange=map.rangeToObstacleFromGridPosition(i, j, heading)*100; // get the actual range from the map

			/*
			 * Separate the conditions depending on the distance given by the sensor and the actual distance
			 */
			if(range <= beginningRange){
				if(coordRange <= beginningRange){
					newProbability(p, fullProbability);
				}
				else {
					//						float newProbability = 0;
					//						float oldProbability = locs.get(p).getProbability();
					//						locs.get(p).setProbability(newProbability*oldProbability);
					newProbability(p, zeroProbability);
					//System.out.println("**i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());

				}
			}
			else if(range > beginningRange && range <= doubleCellRange) {
				if(coordRange <= beginningRange)  {
					//						float newProbability = 0;
					//						float oldProbability = locs.get(p).getProbability();
					//						locs.get(p).setProbability(newProbability*oldProbability);
					newProbability(p, zeroProbability);
					//System.out.println("***i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());
				}
				else if(coordRange > beginningRange && coordRange <= appropriateRange) {
					//						float newProbability = 0.85f;
					//						float oldProbability = locs.get(p).getProbability();
					//						locs.get(p).setProbability(newProbability*oldProbability);
					newProbability(p, probabilityBig);
					//System.out.println("****i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());
				}
				else if(coordRange > appropriateRange) {
					//						float newProbability = 0.15f;
					//						float oldProbability = locs.get(p).getProbability();
					//						locs.get(p).setProbability(newProbability*oldProbability);
					newProbability(p, probabilitySmall);
					//System.out.println("*****i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());
				}
			}
			else if(range > doubleCellRange) {
				if(coordRange < appropriateRange) {
					//							float newProbability = 0;
					//							float oldProbability = locs.get(p).getProbability();
					//							locs.get(p).setProbability(newProbability*oldProbability);
					newProbability(p, zeroProbability);
					//System.out.println("******i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());
				}
				else {
					//							float newProbability = 1;
					//							float oldProbability = locs.get(p).getProbability();
					//							locs.get(p).setProbability(newProbability*oldProbability);
					newProbability(p, fullProbability);
					//System.out.println("*******i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());
				}
			}
		}

		// Removes the positions with 0 probability
		removeInvalid();

		// Normalise the other locations
		normalise();			

	}

	/*
	 * Helper methods
	 */

	/**
	 * Updates the helper array
	 */
	private void newArrayUpdate(){
		newLocs.clear();
		for(int q = 0; q < locs.size(); q++){
			newLocs.add(locs.get(q));
		}
	}

	/**
	 * Removes locations with 0 probability or that are obstructed
	 */
	private void removeInvalid(){

		newArrayUpdate();

		for(int q = 0; q < newLocs.size(); q++){
			int i = (int)newLocs.get(q).getxCoord();
			int j = (int)newLocs.get(q).getyCoord();

			if(map.isObstructed(i, j) || newLocs.get(q).getProbability() == zeroProbability){

				PosProb pos = newLocs.get(q);
				locs.remove(pos);
			}
		}
	}

	/**
	 * Get the size of a cell
	 * @return The cell size
	 */
	public float getCellSize(){
		return map.getCellSize()*100;
	}

	/**
	 * Get the points
	 * @param i 
	 * @return The points
	 */
	public PosProb getPoints(int i){
		return locs.get(i);
	}

	/**
	 * Get the size
	 * @return The size
	 */
	public int size(){
		return locs.size();
	}

	/**
	 * Count the size of the array
	 * @return - the initial number of elements in the array
	 */
	public int initialSize(){
		int count = 0;
		for(int i=0;i<=map.getXSize();i++){

			for(int j=0;j<=map.getYSize();j++){
				if(!map.isObstructed(i, j)){
					count++;

				}
			}
		}
		return count;
	}

	/**
	 * Get the new size
	 * @return The new size
	 */
	public int sizeNew(){
		return newLocs.size();
	}

	/**
	 * Normalise the probabilities of the locations 
	 */
	private void normalise(){
		float total = sumProbabilities();

		for(int i = 0; i < locs.size(); i++) {
			if(!map.isObstructed(locs.get(i).getxCoord(), locs.get(i).getyCoord()) && locs.get(i).getProbability() != 0.0f) {
				float prob = locs.get(i).getProbability();
				locs.get(i).setProbability(prob/total);
			}
		}
	}

	/**
	 * Sum all the probabilities
	 * @return - sum of the probabilities
	 */
	private float sumProbabilities(){
		float total = 0;
		for(int i = 0 ; i < locs.size(); i++) {
			if(!map.isObstructed(locs.get(i).getxCoord(), locs.get(i).getyCoord()) && locs.get(i).getProbability() != 0.0f) {
				total += locs.get(i).getProbability();
			}
		}

		return total;
	}

	/**
	 * Sets new probability after receiving new information 
	 * @param p - the current element 
	 * @param newProbability - the new probability
	 */
	private void newProbability(int p, float newProbability) {
		float oldProbability = locs.get(p).getProbability();
		locs.get(p).setProbability(newProbability*oldProbability);
	}

	/**
	 * Update the locations of the robot when it is moving
	 * @param heading - the heading that the robot is moving with
	 */
	public void updateLocations(float heading) {
		if(heading==180)
			for(int i=0;i<locs.size();i++) {
				locs.set(i,new PosProb(locs.get(i).getxCoord()-1,locs.get(i).getyCoord(), locs.get(i).getProbability()));
			}
		else if(heading==0)
			for(int i=0;i<locs.size();i++) {
				locs.set(i,new PosProb(locs.get(i).getxCoord() + 1,locs.get(i).getyCoord(), locs.get(i).getProbability()));
			}
		else if(heading==90){
			for(int i=0;i<locs.size();i++) {
				locs.set(i,new PosProb(locs.get(i).getxCoord(),locs.get(i).getyCoord() + 1, locs.get(i).getProbability()));
			}
		}
		else if(heading==-90)
			for(int i=0;i<locs.size();i++) {
				locs.set(i,new PosProb(locs.get(i).getxCoord(),locs.get(i).getyCoord() - 1, locs.get(i).getProbability()));
			}
		else 
			assert false : "Unknown value for enumeration";

		removeInvalid();
	}
}
