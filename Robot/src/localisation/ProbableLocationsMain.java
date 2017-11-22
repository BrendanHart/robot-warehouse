package localisation;

import java.util.ArrayList;

import lejos.geom.Point;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.MapUtils;

/**
 * The probable locations
 */
public class ProbableLocationsMain {

	private GridMap map;
	private float cellSize;
	ArrayList<Point> locs = new ArrayList<Point>();
	
	public ProbableLocationsMain(GridMap map){
		this.map=map;
		this.cellSize = map.getCellSize()*100;
	}
	
	/**
	 * Set the locations 
	 * @param heading The heading
	 * @param range The range
	 */
	public void setLocations(float heading,float range){
		for(int i=0;i<map.getYSize();i++){
			for(int j=0;j<map.getXSize();j++){
				System.out.print(map.rangeToObstacleFromGridPosition(j, i, heading)*100+"      ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Get the current location
	 * @param heading The heading
	 * @param range The range
	 * @param i
	 * @param j
	 * @return The location
	 */
	public double currentLoc(float heading, float range, int i, int j){
		return map.rangeToObstacleFromGridPosition(i, j, heading)*100;
	}
	
	public static void main(String[] args){
		GridMap map = MapUtils.createMarkingWarehouseMap();
		ProbableLocations locsReal = new ProbableLocations(map);
		ProbableLocationsMain locs = new ProbableLocationsMain(map);
//		System.out.println("Initial size: " + locsReal.size());
//		System.out.println("New array size: " + locsReal.sizeNew());
//		locsReal.setLocations(0.0f, 13.5f);
//		System.out.println("--------------------------------------------------------");
//		locsReal.setLocations(90.0f, 72.3f);
//		System.out.println("--------------------------------------------------------");
//		locsReal.setLocations(-90.0f, 72.3f);
//		System.out.println("--------------------------------------------------------");
//		locsReal.setLocations(180.0f, 40.9f);
//		System.out.println("Next size: " + locsReal.size());
		for(int i = 0; i < locsReal.size(); i++){
			System.out.println("i = " + locsReal.getPoints(i).getxCoord() + " j = " + locsReal.getPoints(i).getyCoord() + " prob: " + locsReal.getPoints(i).getProbability());
		}
//		//System.out.println("Next size new array: " + locsReal.sizeNew());
//		locsReal.updateLocations(90.0f);
//		locsReal.updateLocations(90.0f);
//		locsReal.updateLocations(90.0f);
//		locsReal.updateLocations(90.0f);
////		locsReal.setLocations(90.0f, 13.5f);
////		locsReal.setLocations(180.0f, 72.3f);
////		locsReal.setLocations(-90.0f, 72.3f);
////		locsReal.setLocations(0.0f, 72.3f);
//		
//		System.out.println("--------------------------------------------------------");
//		
//		locsReal.setLocations(90.0f, 13.5f);
//		locsReal.setLocations(180.0f, 72.3f);
//		locsReal.setLocations(-90.0f, 40.3f);
//		locsReal.setLocations(0.0f, 42.3f);
//		
//		System.out.println("Next size: " + locsReal.size());
//		for(int i = 0; i < locsReal.size(); i++){
//			System.out.println("i = " + locsReal.getPoints(i).getxCoord() + " j = " + locsReal.getPoints(i).getyCoord() + " prob: " + locsReal.getPoints(i).getProbability());
//		}
////		//locsReal.setLocations(90.0f, 15.5f);
////		//System.out.println("After moving to wall size: " + locsReal.size());
////		for(int i = 0; i < locsReal.size(); i++){
////			//System.out.println("i = " + locsReal.getPoints(i).getX() + " j = " + locsReal.getPoints(i).getY());
////		}
//		locsReal.updateLocations(180.0f);
//		locsReal.updateLocations(180.0f);
//		locsReal.updateLocations(180.0f);
//		
//		System.out.println("Next size: " + locsReal.size());
//		for(int i = 0; i < locsReal.size(); i++){
//			System.out.println("i = " + locsReal.getPoints(i).getxCoord() + " j = " + locsReal.getPoints(i).getyCoord() + " prob: " + locsReal.getPoints(i).getProbability());
//		}
//		locsReal.setLocations(180.0f, 19.5f);
//		System.out.println("final size: " + locsReal.size());
//		for(int i = 0; i < locsReal.size(); i++){
//			//System.out.println("i = " + locsReal.getPoints(i).getX() + " j = " + locsReal.getPoints(i).getY());
//		}
////		//locs.setLocations(0, 100);
////		//System.out.println("size of the cell: " + map.getCellSize());
////		
	}
}
