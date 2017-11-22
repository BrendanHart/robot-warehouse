package networking;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import Objects.Sendable.DropOffPoint;
import Objects.Sendable.Move;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SendableObject;
import Objects.Sendable.SingleTask;


/**
 * Receive from the server
 */
public class ClientReceiver extends Thread {

	private boolean alive = true;
	private DataInputStream fromServer;
	private ArrayList<SendableObject> commands = new ArrayList<SendableObject>();

	public ClientReceiver(DataInputStream fromServer) {
		this.fromServer = fromServer;
	}

	/**
	 * Forever trying to read two messages at a time from the server, to deduce
	 * new commands.
	 */
	@Override
	public void run() {
		while (alive) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("INTERRUPTED.");
			}

			try {
				String fullComm = fromServer.readUTF();
				Thread.sleep(1000);
				Object[] splitComm = Splitter.split(fullComm);
				String type = (String) splitComm[0];
				Object[] objParams = new Object[splitComm.length-1];
				for(int i = 1; i < splitComm.length; i++){
					objParams[i-1] = splitComm[i];
				}
				figureType(type, objParams);				

			} catch (IOException e) {
				out("MyListener noticed the server died. Shutting everything down.");
				System.exit(3);
			} catch (NullPointerException e) {
				// Stops .readUTF from throwing errors.
			} catch (InterruptedException e) {
				out("Connection broke.");
			}
		}
	}

	/**
	 * Reforms the Strings sent from the server into SendableObjects that are added then added to the commands list.
	 * @param The name of the class.
	 * @param The parameters of that class.
	 * @throws InterruptedException 
	 */
	private synchronized void figureType(String type, Object[] parameters) throws InterruptedException {
		if(type.equals("Console")){
			String message = "";
			for(Object param : parameters){
				message += param;
			}
			out(message);
			Thread.sleep(1000);
		}
		else {
			SendableObject newObj = null;
			if(type.equals("Move")){
				char direction = parameters[0].toString().charAt(0);				
				Point location = new Point((Integer)parameters[1], (Integer)parameters[2]);

				newObj = new Move(direction, location);
				Thread.sleep(200);
			}
			else if(type.equals("SingleTask")){
				newObj = new SingleTask((String) parameters[0].toString(), (Integer) parameters[1], new Point((Integer) parameters[2], (Integer) parameters[3]));
			}
			else if(type.equals("RobotInfo")){
				newObj = new RobotInfo((String) parameters[0].toString());
			}
			else if(type.equals("DropOffPoint")){
				newObj = new DropOffPoint((int) parameters[0], (int) parameters[1]);
			}

			if(newObj == null){
				out("Error creating new object. Didn't know how to deal with: " + type);
			}
			else {
				addComm(newObj);
			}
		}
	}

	/**
	 * Ends the thread.
	 */
	public void shutdown(){
		alive = false;
	}

	// ************************ ARRAYLIST ACCESSORS **************************

	/**
	 * Only done so that access to the commandsForInterface ArrayList is always
	 * synchronised.
	 * 
	 * @param comm The command to add.
	 */
	synchronized private void addComm(SendableObject comm) {
		commands.add(comm);
	}

	/**
	 * Takes the next message from the message list.
	 * @return
	 */
	synchronized public SendableObject popCommand() {
		if (commands.isEmpty()) {
			return null;
		} else {
			SendableObject comm = commands.get(0);
			commands.remove(0);
			return comm;
		}
	}

	// ******************** HELPER COMMANDS **********************

	private void out(Object n) {
		System.out.println("" + n);
	}
}
