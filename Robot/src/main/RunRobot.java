package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import Objects.Sendable.DropOffPoint;
import Objects.Sendable.Move;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SendableObject;
import Objects.Sendable.SingleTask;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import networking.ClientReceiver;
import networking.ClientSender;
import robotInterface.RobotInterface;
import robotMotionControl.RobotMotion;

/**
 * The main class to run all the robot side code
 */
public class RunRobot {

	private ClientReceiver receiver;

	private String name;

	private RobotInterface theInterface;

	public RunRobot(String[] args) {

		// Sets up and waits for a Bluetooth connection
		System.out.println("Waiting for Bluetooth connection...");
		BTConnection connection = Bluetooth.waitForConnection();
		System.out.println("OK!");
		LCD.clear();

		// Opens the data streams
		DataInputStream in = connection.openDataInputStream();
		DataOutputStream out = connection.openDataOutputStream();
		ClientSender.setStream(out);
		receiver = new ClientReceiver(in);  
		receiver.start();

		while (receiver.isAlive()) {			

			// Goes through all available instructions and executes them.
			SendableObject comm = null;

			while((comm = receiver.popCommand()) != null)
			{
				if(comm instanceof Move){
					RobotMotion.move((Move)comm);
				}
				else if(comm instanceof SingleTask || comm instanceof DropOffPoint){
					theInterface.add(comm);
				}
				else if(comm instanceof RobotInfo){
					name = ((RobotInfo) comm).getName();
					theInterface = new RobotInterface(name);
				}
			}
		}
		out("MyClient no longer running");
	}

	/**
	 * Main method to run everything
	 * @param args
	 */
	public static void main(String[] args) {
		new RunRobot(args);
	}

	// ********************** HELPER METHODS *********************

	/**
	 * Helper command to print out objects for debugging
	 * @param n Object to print
	 */
	private void out(Object n) {
		System.out.println(""+n);
	}

}
