package robotInterface; 

import java.awt.Point;

import Objects.Direction;
import Objects.Sendable.CompleteReport;
import Objects.Sendable.DropOffPoint;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SingleTask;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import networking.ClientSender;

/**
 * The class that handles the robot screen and user interaction to pick-up and drop-off items
 * @author rkelly
 *
 */
public class RobotInterface {

	//	private ArrayList<String> itemsHeld;
	private String robotName;

	public RobotInterface(String rName) {
		robotName = rName;
		//		itemsHeld = new ArrayList<String>();
		getDetails();
		//ClientSender.send(ri);
	}

	public RobotInterface(String name, Point location, Direction dir){
		robotName = name;
		//		itemsHeld = new ArrayList<String>();
		if (confirm((int)location.getX(), (int)location.getY())){
			RobotInfo info = new RobotInfo(name, location, dir);
			ClientSender.send(info);
		}
		else getDetails();
	}

	/**
	 * Output to the screen to take in the location and direction of the robot
	 * @param name the robot's name
	 * @return A StartUpItem containing name, x, y and direction
	 */
	private void getDetails() {
		LCD.clear();
		int xVal = 0; //initial values of x, y and location
		int yVal = 0;
		char d = 'N';
		Direction dir = Direction.NORTH;
		LCD.drawString("- x: " + xVal, 0, 1); //output the instructions to the screen
		LCD.drawString("y: " + xVal, 2, 3);
		LCD.drawString("Direction: " + d, 2, 5);
		int which = 0; //indicator
		int pressed;
		while (which < 3) { //while not all details have been confirmed
			pressed = Button.waitForAnyPress(); //wait for a button press
			if(pressed == Button.ID_RIGHT) { //if the user is increasing the value
				if(which == 0) { //if we are currently editing x
					LCD.clear(1);
					xVal++; //increase
					LCD.drawString("- x: " + xVal, 0, 1);
				}
				else if(which == 1) { //if we are currently editing y
					LCD.clear(3);
					yVal++; //increase
					LCD.drawString("- y: " + yVal, 0, 3);
				}
				else if(which == 2) { //if we are currently editing direction
					LCD.clear(5);
					if(d == 'N') { //change appropriately
						d = 'E';
						dir = Direction.EAST;
					}
					else if(d == 'E') {
						d = 'S';
						dir = Direction.SOUTH;
					}
					else if(d == 'S') {
						d = 'W';
						dir = Direction.WEST;
					}
					else {
						d = 'N';
						dir = Direction.NORTH;
					}
					LCD.drawString("- Direction: " + d, 0, 5);
				}
			}
			else if(pressed == Button.ID_LEFT) { //if the user is decreasing the value
				if(which == 0) { //see above part of if statement
					LCD.clear(1);
					xVal--;
					LCD.drawString("- x: " + xVal, 0, 1);
				}
				else if(which == 1) {
					LCD.clear(3);
					yVal--;
					LCD.drawString("- y: " + yVal, 0, 3);
				}
				else if(which == 2) {
					LCD.clear(5);
					if(d == 'N') {
						d = 'W';
						dir = Direction.WEST;
					}
					else if(d == 'E') {
						d = 'N';
						dir = Direction.NORTH;
					}
					else if(d == 'S') {
						d = 'E';
						dir = Direction.EAST;
					}
					else {
						d = 'S';
						dir = Direction.SOUTH;
					}
					LCD.drawString("- Direction: " + d, 0, 5);
				}
			}
			else if(pressed == Button.ID_ENTER) { //if the information is being confirmed
				which++; //move onto the next detail
				if(which == 1) {
					LCD.clear(1);
					LCD.drawString("x: " + xVal, 2, 1);
					LCD.clear(3);
					LCD.drawString("- y: " + yVal, 0, 3);
				}
				else if (which == 2) {
					LCD.clear(3);
					LCD.drawString("y: " + yVal, 2, 3);
					LCD.clear(5);
					LCD.drawString("- Direction: " + d, 0, 5);
				}
			}
			else { //if we want to edit the previous information
				which--; //move to the previous detail
				if(which == 0) {
					LCD.clear(3);
					LCD.drawString("y: " + yVal, 2, 3);
					LCD.clear(1);
					LCD.drawString("- x: " + xVal, 0, 1);
				}
				else if (which == 1) {
					LCD.clear(3);
					LCD.drawString("- y: " + yVal, 0, 3);
					LCD.clear(5);
					LCD.drawString("Direction: " + d, 2, 5);
				}
			}
		}
		LCD.clear();
		ClientSender.send(new RobotInfo(robotName, new Point(xVal, yVal), dir));
	}

	/**
	 * Add a n Object to the interface so a pick-up or drop-off can be executed
	 * @param command the Object to add
	 */
	public void add(Object command) {
		SingleTask st;
		DropOffPoint dp;
		if(command instanceof SingleTask) {
			st = (SingleTask) command;
			pickup(st);
		}
		else if(command instanceof DropOffPoint) {
			dp = (DropOffPoint) command;
			dropoff(dp);
		}
	}
	/**
	 * Allow the user to interact with the robot to drop-off items
	 * @param where
	 */
	private void dropoff(DropOffPoint where) {
		LCD.clear();
		LCD.drawString("DP => drop off", 0, 0); //testing
		boolean correct = confirm(where.getX(), where.getY());
		if(correct) { //if the location is correct
			//			LCD.drawString("I have:", 1, 0);
			//			int y = 1;
			//			for(String t : itemsHeld) {
			//				if(y <= 7) { //if it can fit on the display
			//					LCD.drawString(t, 2, y); //Display a single quantity of items held
			//				}
			//				else { //otherwise, add to buffer
			//					buffer.add(t);
			//				}
			//				y++;
			//			}
			//			if(y <= 6) { //if instructions can also fit on the screen
			LCD.drawString("Press ENTER", 3, 3);  //Display instructions
			LCD.drawString("to drop-off", 3, 4);
			//			}
			//			else { //otherwise add them separately to buffer
			//				buffer.add("Press ENTER");
			//				buffer.add("to drop-off");
			//			}
			int pressed = Button.waitForAnyPress(); //Record the pressed button
			while(pressed != Button.ID_ENTER) {		//if it isn't enter
				pressed = Button.waitForAnyPress();	//keep waiting, until enter is pressed
			}
			CompleteReport report = new CompleteReport(false, true, false);
			ClientSender.send(report);
			LCD.clear();
			//			while(!itemsHeld.isEmpty()) {
			//				itemsHeld.remove(0);
			//			}
		}
		else { //if the location isn't correct
			getDetails();
			CompleteReport report = new CompleteReport(false, false, false); //return saying drop-off not completed
			ClientSender.send(report);
		}
		LCD.clear();
	}

	/**
	 * Allow the user to interact with the robot to pick-up items
	 * @param item
	 */
	private void pickup(SingleTask item) {
		String id = item.getItemID();
		int quantity = item.getQuantity();
		LCD.clear();
		LCD.drawString("ST => pick up", 0, 0); // testing
		boolean correct = confirm((int)item.getLocation().getX(), (int)item.getLocation().getY());
		boolean cancelled = false;
		if(correct) {		//^ dummy data as I don't know currently how I will get the location
			int x = 2;
			int y = 3;
			LCD.drawString("I need " + quantity + "x " + id + ".", x, y); //Output message
			LCD.drawString("ENTER: add", 0, 6);
			LCD.drawString("ESC: cancel", 0, 7);
			int i = quantity;
			int pressed;
			while(i > 0 && !cancelled) {
				pressed = Button.waitForAnyPress(10000); //wait for button to be pressed and store which it is
				LCD.clear(y);		
				if(pressed == 0) {
					Sound.beep();
				}
				else if(pressed == Button.ID_ENTER) { //if it is the enter button
					i--; //decrease the number needed
				}
				else if(pressed == Button.ID_ESCAPE) { //otherwise
					cancelled = true;
				}
				LCD.drawString("I need " + i + "x " + id + ".", x, y); //Update on screen how many needed
			}
			LCD.clear();
			CompleteReport report;
			if(!cancelled) {
				//				itemsHeld.add(quantity + "x " + id);
				report = new CompleteReport(true, true, false); //to add to CommandHolder
			}
			else {
				report = new CompleteReport(true, false, true);
			}
			ClientSender.send(report);
		}
		else { //if the location is incorrect, send a report saying the pickup wasn't completed
			getDetails();
			CompleteReport report = new CompleteReport(true, false, false);
			ClientSender.send(report);
		}
		LCD.clear();
	}

	/**
	 * Allow the user to confirm that the robot is in the correct location (before picking up or dropping off)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return true if the location is correct, false otherwise
	 */
	private boolean confirm(int x, int y) {
		LCD.clear();
		boolean confirmed = false;
		boolean ySelected = true;
		int pressed;
		Sound.twoBeeps();
		while(!confirmed) { //while the location hasn't been confirmed
			LCD.drawString("Am I at (" + x + ", " + y + ")?", 1, 3); //output asking if it is correct
			if(ySelected) {	//if yes is currently selected
				LCD.drawString("-> Y / N", 3, 5); //clearly display that
			}
			else {
				LCD.drawString("Y / N <-", 6,  5); //display a pointer to no
			}
			pressed = Button.waitForAnyPress(); //wait for a button to be pressed
			if(pressed == Button.ID_ENTER) { //if the enter button is pressed
				confirmed = true;	//confirm location (don't loop again)
			}
			else if(pressed == Button.ID_LEFT) { //if the left arrow button is pressed
				ySelected = true; //yes is currently selected
			}
			else if(pressed == Button.ID_RIGHT) { //if the right arrow button is pressed
				ySelected = false; //no is currently selected
			}
			LCD.clear(); //clear the screen after each loop
		}
		return ySelected; //after confirmation, return whether yes was selected
	}

	/*
	public static void main(String[] args) { 

		// CAN USE THIS CODE IN THE TEST_SCRIPTS BRANCH
		Button.waitForAnyPress();
		CommandHolder h = new CommandHolder();
		RobotInterface i = new RobotInterface("dshfjdshf");
		i.getDetails();
		Delay.msDelay(1000);
		i.add(new SingleTask("aa", 5, new Point(1, 2)));
		Delay.msDelay(1000);
		i.add(new SingleTask("ac", 3, 6, 6));
		Delay.msDelay(1000);
		i.add(new SingleTask("ae", 4, 4, 2));
		Delay.msDelay(1000);
		i.add(new DropoffPoint(6, 2));
	}
	*/

}
