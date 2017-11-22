package robotMotionControl;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;
/**
 * Class for testing the light values
 * nothing interesting here
 */
public class SensorTest {

	private static float leftValue;
	private static float rightValue;
	private static LightSensor left = new LightSensor(SensorPort.S1);
	private static LightSensor right = new LightSensor(SensorPort.S3);
	private static boolean running=true;
	
	
	public static void main(String[] args){
		run();
	}
	
	public static void run(){
		while(running){
			 
			generateLightValues();
		
		    LCD.drawString(leftValue+" "+rightValue+" ", 0, 0);
		    Delay.msDelay(50);
		}
		
	}
	public void stop(){
		running=false;
	}

	private static void generateLightValues(){
		leftValue = left.getLightValue();
		rightValue = right.getLightValue();
	}
}

