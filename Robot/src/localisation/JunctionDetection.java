package localisation;

import Objects.Direction;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;

/**
 * Behaviour for detecting junctions
 * @authors Simeon Kostadinov and Lyubomir Pashev
 */
public class JunctionDetection implements Behavior {

	/*
	 * Initialize fields
	 */
	private OpticalDistanceSensor ranger; // range sensor
	private LightSensor left; // left sensor
	private LightSensor right; // right sensor
	private int leftValue; // value from the left sensor
	private int rightValue; // value from the right sensor
	private DifferentialPilot pilot; // the robot pilot

	//Light value threshold
	private int threshold; // the light threshold

	//Moves
	private int NORTH = 0; // direction North
	private int WEST = 1; // direction West
	private int SOUTH = 2; // direction South
	private int EAST = 3; // direction East
	private int robotDirection = NORTH; // At the beginning the robotDirection is North
	private float NORTH_DISTANCE; 
	private float SOUTH_DISTANCE;
	private float WEST_DISTANCE;
	private float EAST_DISTANCE;
	private float DISTANCE;
	private boolean firstTime=false;
	private float heading; // current heading of the robot

	private float cellSize = Localisation.locs.getCellSize(); // get the size of the cell

	/**
	 * Set the speed of the robot
	 * @param pilot - the robot pilot
	 * @param left - left sensor
	 * @param right - right sensor
	 * @param speed - speed of the robot
	 * @param distanceSensor - range sensor
	 * @param threshold - threshold for the light sensor
	 */
	public JunctionDetection(DifferentialPilot pilot, LightSensor left, LightSensor right, double speed, OpticalDistanceSensor distanceSensor, int threshold){
		this.left=left;
		this.right=right;
		this.pilot=pilot;
		this.ranger=distanceSensor;
		this.threshold = threshold;
		pilot.setTravelSpeed(speed);
	}

	/**
	 * Take control of when detects a junction
	 * @return - true if there is a junction and the array of coordinates has more than one element
	 */
	@Override
	public boolean takeControl(){

		generateLightValues();
		return (leftValue < threshold) && (rightValue < threshold) && Localisation.locs.size()>1;
	}


	/**
	 * Action of junction behaviour 
	 * Rotates on 360 degrees when detects a junction and calculates the possible locations
	 */
	public void action() {
		pilot.travel(0.1f);
		if(!firstTime || ranger.getRange()<cellSize){

			for(int i=0;i<4;i++){
				if(checkEnd(Localisation.locs.size())) //check if a location is found and ends the behaviour
					return;
				executeMove(RotationDirections.LEFT);
				Delay.msDelay(1000);

			}
			firstTime = true;
		}

		if(checkEnd(Localisation.locs.size())) //check if a location is found and ends the behaviour
			return;

		// Make a decision where to turn when sees a wall
		if(ranger.getRange()<cellSize){
			if(heading == 90){
				turnDecision(WEST_DISTANCE, EAST_DISTANCE);

			}else if(heading == 180){
				turnDecision(NORTH_DISTANCE, SOUTH_DISTANCE);

			}else if(heading == -90){
				turnDecision(EAST_DISTANCE, WEST_DISTANCE);

			}else if(heading == 0){
				turnDecision(SOUTH_DISTANCE, NORTH_DISTANCE);
			}

		}else{
			executeMove(RotationDirections.FORWARD);
			Localisation.locs.updateLocations(heading); // update the current probable locations while moving
		}

		if(checkEnd(Localisation.locs.size())) //check if a location is found and ends the behaviour
			return;

		LCD.clear();
		LCD.drawString("" + Localisation.locs.size(), 0, 7);

		Delay.msDelay(100);
	}

	/**
	 * The junction behaviour is ended when the size of the array is less than or equal to 1 so the suppress method is empty
	 */
	@Override
	public void suppress() {

	}

	/**
	 * Helper methods
	 */

	//Turns left
	private void moveLeft(){
		pilot.rotate(126);
	}
	//Turns right
	private void moveRight(){
		pilot.rotate(-126);			
	}
	//Continues going forward
	private void moveForward(){
		pilot.forward();
	}

	/**
	 * Generate the values of the left and right light sensors
	 */
	private void generateLightValues(){
		leftValue = left.getLightValue();
		rightValue = right.getLightValue();
	}

	/**
	 * Rotates the robot on 360 degrees and calculates the distances on each rotation
	 * @param move - turn left or right
	 */
	private void executeMove(int move){
		setDirections();
		//LCD.drawString(DISTANCE + " ", 0, testCount);
		Localisation.locs.setLocations(heading, DISTANCE);
		if(move==RotationDirections.LEFT){

			moveLeft();
			if(robotDirection==EAST)
				robotDirection=NORTH;
			else			
				robotDirection+=1;
		}
		else if(move==RotationDirections.RIGHT){
			moveRight();
			if(robotDirection == NORTH)
				robotDirection = EAST;
			else
				robotDirection-=1;
		}
		else
			moveForward();

		setDirections();

	}

	/**
	 * Make a decision where to turn when sees a wall 
	 * @param leftPosition - which direction is on the left of the robot
	 * @param rightPosition - which direction is on the right of the robot
	 */
	private void turnDecision(float leftPosition, float rightPosition){
		if(leftPosition > cellSize && rightPosition > cellSize){ 
			if(leftPosition < rightPosition){
				executeMove(RotationDirections.LEFT);
				Localisation.locs.updateLocations(heading);
			}else{
				executeMove(RotationDirections.RIGHT);
				Localisation.locs.updateLocations(heading);
			}
		}else if(leftPosition > cellSize){
			executeMove(RotationDirections.LEFT);
			Localisation.locs.updateLocations(heading);
		}else if(rightPosition > cellSize){
			executeMove(RotationDirections.RIGHT);
			Localisation.locs.updateLocations(heading);
		}else{
			executeMove(RotationDirections.LEFT);
			executeMove(RotationDirections.LEFT);
			Localisation.locs.updateLocations(heading);
		}
	}

	/**
	 * Sets the heading and the distance when the robot is facing different directions
	 */
	private void setDirections(){
		if(robotDirection==NORTH){
			heading = 90;
			NORTH_DISTANCE=ranger.getRange();
			DISTANCE=ranger.getRange();
		}
		else if(robotDirection==EAST){
			heading = 0;
			EAST_DISTANCE=ranger.getRange();
			DISTANCE=ranger.getRange();
		}
		else if(robotDirection==SOUTH){
			heading = -90;
			SOUTH_DISTANCE=ranger.getRange();
			DISTANCE=ranger.getRange();
		}
		else if(robotDirection==WEST){
			heading = 180;
			WEST_DISTANCE=ranger.getRange();
			DISTANCE=ranger.getRange();
		}
	}

	/**
	 * Check if the task is complete
	 * @param size - size of the array
	 * @return - true if the size of the array is less than or equal to 1
	 */
	private boolean checkEnd(int size){
		return size <= 1;
	}

	/**
	 * Called when the robot finds the location
	 * @return - the direction of the robot when it finds its location
	 */
	public Direction getDir(){
		if(robotDirection==NORTH)
			return Direction.NORTH;
		else if(robotDirection==EAST)
			return Direction.EAST;
		else if(robotDirection==SOUTH)
			return Direction.SOUTH;
		else
			return Direction.WEST;
	}
}

