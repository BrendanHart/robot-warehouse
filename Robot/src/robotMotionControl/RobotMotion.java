package robotMotionControl;

import Objects.Sendable.Move;
import Objects.Sendable.MoveReport;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import networking.ClientSender;
import rp.config.WheeledRobotConfiguration;
import rp.robotics.DifferentialDriveRobot;
/**
 * The class that handles the robot's movement
 * @author Lyubomir Pashev
 *
 */
public class RobotMotion {

	// Light sensors
	private static LightSensor left = new LightSensor(SensorPort.S1);
	private static LightSensor right = new LightSensor(SensorPort.S3);
	private static float leftValue;
	private static float rightValue;
	private static final int threshold = 41;

	// Robot configuration
	private static final WheeledRobotConfiguration OUR_BOT = new WheeledRobotConfiguration(0.056f, 0.120f, 0.230f, Motor.B, Motor.C);
	private static final DifferentialPilot pilot = new DifferentialDriveRobot(OUR_BOT).getDifferentialPilot();

	/**
	 * Executes a given move
	 * @param move the current move
	 */
	public static void move(Move move) {
		pilot.setTravelSpeed(0.18);

		char direction = move.getDirection();

		if(direction == 'f'){
			LCD.drawString("Forward", 0, 7);
			forward();
		}
		else if(direction == 'b'){
			LCD.drawString("Backward", 0, 7);
			backward();
		}
		else if(direction == 'r'){
			LCD.drawString("Right", 0, 7);
			right();
		}
		else if(direction == 'l'){
			LCD.drawString("Left", 0, 7);
			left();
		}

		ClientSender.send(new MoveReport(true));
		LCD.drawString("Sent Report", 0, 0);

	}

	/**
	 * Moves left
	 */
	private static void left() {
		pilot.travel(0.1);
		pilot.rotate(128);
		forward();
	}

	/**
	 * Moves right
	 */
	private static void right() {
		pilot.travel(0.1);
		pilot.rotate(-128);
		forward();
	}

	/**
	 * Moves backwards
	 */
	private static void backward() {
		pilot.rotate(252);
		forward();
	}

	/**
	 * Moves forward
	 */
	private static void forward() {
		pilot.forward();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		generateLightValues();
		while(leftValue > threshold || rightValue > threshold){
			float diff = leftValue - rightValue;			
			pilot.steer(-diff);
			generateLightValues();
		}
		pilot.stop();
		LCD.drawString("I found a junction", 0, 6);
	}

	/**
	 * Generates light values
	 */
	private static void generateLightValues(){
		leftValue = left.getLightValue();
		rightValue = right.getLightValue();
	}

}
