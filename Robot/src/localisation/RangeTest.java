package localisation;

import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.util.Delay;
import rp.config.RobotConfigs;
import rp.config.WheeledRobotConfiguration;
import rp.systems.WheeledRobotSystem;

/**
 * Test the range
 */
public class RangeTest{
	
	private WheeledRobotSystem system;
	private boolean running=true;
	private OpticalDistanceSensor distanceSensor;



	public RangeTest(WheeledRobotConfiguration config,OpticalDistanceSensor distanceSensor){
		 this.system= new WheeledRobotSystem(config);
		 this.distanceSensor=distanceSensor;
		 
		
	}
	
	public static void main(String[] args){
		RangeTest rangeTest = new RangeTest((WheeledRobotConfiguration) RobotConfigs.CASTOR_BOT,new OpticalDistanceSensor(SensorPort.S2));
		rangeTest.run();
		
	}
	
	public void run() {
		while(running){
			LCD.drawString(String.valueOf(distanceSensor.getRange()), 0, 0);
			Delay.msDelay(50);
			
		}
		
	}
	public void stop()
	{
		running =false;
	}
	
}