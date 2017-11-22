package localisation;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;

/**
 * The default behaviour for following a line
 * @authors Simeon Kostadinov and Lyubomir Pashev
 */
public class LineFollow implements Behavior {

	/*
	 * Initialize fields
	 */
	private LightSensor left; // left light sensor
	private LightSensor right; // right light sensor
	private int leftValue; // value from the left sensor
	private int rightValue; // value from the right sensor
	private int threshold; // threshold for the light sensors
	private boolean suppress=false;
	private DifferentialPilot pilot; // the robot pilot
	private OpticalDistanceSensor range;

	/**
	 * Set the speed of the robot
	 * @param pilot - the robot pilot
	 * @param left - left sensor
	 * @param right - right sensor
	 * @param speed - speed of the robot
	 * @param threshold - threshold for the light sensor
	 */
	public LineFollow(DifferentialPilot pilot, LightSensor left,LightSensor right, double speed, OpticalDistanceSensor range,  int threshold){
		this.pilot = pilot;
		this.left=left;
		this.right=right;
		this.threshold = threshold;
		this.range = range;
		pilot.setTravelSpeed(speed);
	}

	/**
	 * Takes control of the behaviour when the size of the array is more than 1 and stops when a location is found
	 * @return - true if the size of the array is more than 1 
	 */
	@Override
	public boolean takeControl() {
		if(Localisation.locs.size()<=1 && range.getRange() > Localisation.locs.getCellSize()){
			pilot.stop();
			LCD.drawString("Stopping the robot", 0, 2);
		}
		LCD.drawString("" + Localisation.locs.size(), 0, 3);
		return Localisation.locs.size()>1;

	}


	/**
	 * Keeps the robot moving on the line
	 */
	@Override
	public void action() {

		pilot.forward();
		generateLightValues();

		while((leftValue > threshold || rightValue > threshold) && !suppress){
			float diff = leftValue - rightValue;			
			pilot.steer(-diff);
			generateLightValues();
		}

		pilot.stop();
		suppress = false;
		Delay.msDelay(100);

	}

	/**
	 * Ends the behaviour when a junction is found
	 */
	@Override
	public void suppress() {
		pilot.stop();
		suppress = true;

	}

	/**
	 * Generate the values of the left and right light sensors
	 */
	private void generateLightValues(){
		leftValue = left.getLightValue();
		rightValue = right.getLightValue();
	}
}