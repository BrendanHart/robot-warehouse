package main;

import java.awt.Point;
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
import localisation.Localisation;
import networking.ClientReceiver;
import networking.ClientSender;
import robotInterface.RobotInterface;
import robotMotionControl.RobotMotion;

/**
 * The main class to run all the robot side code (+ LOCALISATION)
 */
public class RunRobotLoc {

	private ClientReceiver receiver;

	private String name;

	private RobotInterface theInterface;

	public RunRobotLoc(String[] args) {

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

			while ((comm = receiver.popCommand()) != null) {
				if (comm instanceof Move) {
					RobotMotion.move((Move) comm);
				} else if (comm instanceof SingleTask || comm instanceof DropOffPoint) {
					theInterface.add(comm);
				} else if (comm instanceof RobotInfo) {
					name = ((RobotInfo) comm).getName();
					Localisation loco = new Localisation();
					System.out.println(Localisation.locs.size());
					while (Localisation.locs.size() > 1) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
					if (Localisation.locs.size() == 1) {
						System.out.println("Localisation successful" );
						theInterface = new RobotInterface(name, new Point(Localisation.locs.getPoints(0).getxCoord(), Localisation.locs.getPoints(0).getyCoord()) , loco.getDir());
//						RobotInfo newInfo = new RobotInfo(name, new Point(Localisation.locs.getPoints(0).getxCoord(), Localisation.locs.getPoints(0).getyCoord()) , loco.getDir());
//						ClientSender.send(newInfo);
					} else {
						System.out.println("Localisation fail");	
					}
					
				}
			}
		}
		out("MyClient no longer running");
	}

	/**
	 * Main method to run everything
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new RunRobotLoc(args);
	}

	// ********************** HELPER METHODS *********************

	/**
	 * Helper command to print out objects for debugging
	 * 
	 * @param n
	 *            Object to print
	 */
	private void out(Object n) {
		System.out.println("" + n);
	}

}
