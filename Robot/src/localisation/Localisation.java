package localisation;

import Objects.Direction;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import rp.config.WheeledRobotConfiguration;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.mapping.MapUtils;

/**
 * Localisation 
 * @authors Simeon Kostadinov and Lyubomir Pashev
 *
 */
public class Localisation {

	private Behavior junction; // junction behavior
	private Behavior line; // line behavior
	private Arbitrator arby; // arbitrator to store the behaviors
	public static ProbableLocations locs; // static variable for the ProbableLocations
	public Localisation() {
		final double speed = 0.18; // speed of the robot
		final int threshold = 41; // light sensor threshold

		// Configurations of the robot
		WheeledRobotConfiguration OUR_BOT = new WheeledRobotConfiguration(0.056f, 0.120f, 0.230f, Motor.B, Motor.C);
		// Robot's pilot
		DifferentialPilot pilot = new DifferentialDriveRobot(OUR_BOT).getDifferentialPilot();
		// Left light sensor
		LightSensor left = new LightSensor(SensorPort.S1);
		// Right light sensor
		LightSensor right = new LightSensor(SensorPort.S3);
		// Distance sensor
		OpticalDistanceSensor range = new OpticalDistanceSensor(SensorPort.S2);
		// set the map to be the one from MapUtils
		locs  = new ProbableLocations(MapUtils.createMarkingWarehouseMap());

		line = new LineFollow(pilot, left, right, speed, range, threshold);
		junction = new JunctionDetection(pilot, left, right, speed, range, threshold);

		// set the value true so that when the two behaviors ended to end the arbitrator
		arby = new Arbitrator(new Behavior[] {line,junction}, true);
		arby.start();	

	}

	/**
	 * This method is used to pass the direction of the robot to RobotInterface
	 * @return - direction of the robot when founds the location
	 */
	public Direction getDir(){
		return ((JunctionDetection)junction).getDir();
	}
}

