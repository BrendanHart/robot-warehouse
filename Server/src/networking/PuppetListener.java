package networking;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import Objects.Direction;
import Objects.Sendable.CompleteReport;
import Objects.Sendable.MoveReport;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SendableObject;

public class PuppetListener extends Thread {

	private DataInputStream fromRobot;
	ArrayList<SendableObject> commands = new ArrayList<SendableObject>();
	private boolean alive = true;
	private String name;
	
	public PuppetListener(DataInputStream fromRobot, String name){
		this.fromRobot = fromRobot;
		this.name = name;
	}

	/**
	 * Waits for a message from the robot is associated with, and adds it to the ArrayList commands.
	 */
	@Override
	public void run(){
		while(alive){
			try {
				String fullComm = fromRobot.readUTF();
				out("[RECEIVED] " + name + " | " + fullComm);
				Object[] splitComm = Splitter.split(fullComm);
				String type = (String) splitComm[0];
				Object[] objParams = new Object[splitComm.length-1];
				for(int i = 1; i < splitComm.length; i++){
					objParams[i-1] = splitComm[i];
				}
				figureType(type, objParams);
			}
			catch (IOException e) {
				out("Connection died, shutting down.");
				alive = false;
			}
		}
	}
	
	private synchronized void figureType(String type, Object[] parameters) {
		if(type.equals("Console")){
			String message = "";
			for(Object param : parameters){
				message += param;
			}
			out(message);
		}
		else {
			SendableObject newObj = null;
			if(type.equals("RobotInfo")){
				newObj = new RobotInfo((String) parameters[0], new Point((int) parameters[1], (int) parameters[2]), Enum.valueOf(Direction.class, (String)parameters[3]));
			}
			else if(type.equals("MoveReport")){
				newObj = new MoveReport((boolean) parameters[0]);
			}
			else if(type.equals("CompleteReport")){
				newObj = new CompleteReport((boolean) parameters[0], (boolean) parameters[1], (boolean) parameters[2]);
			}
			
			if(newObj == null){
				out("Error creating new object. Didn't know how to deal with: " + type);
			}
			else {
				addComm(newObj);
			}
		}
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
	
	// Helper command
	private void out(Object n) {
		System.out.println(""+n);
	}
}
