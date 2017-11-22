package routeExecution;

import java.awt.Point;

import java.util.PriorityQueue;
import java.util.Vector;

import Objects.AllPuppets;
import Objects.AllRobots;
import Objects.Direction;
import Objects.GlobalClock;

import Objects.Job;
import Objects.WarehouseMap;
import Objects.Sendable.CompleteReport;
import Objects.Sendable.DropOffPoint;
import Objects.Sendable.Move;
import Objects.Sendable.MoveReport;
import Objects.Sendable.SingleTask;
import jobInput.JobProcessor;
import jobSelection.Selection;
import routePlanning.pathFinding.PathFinding;
import warehouseInterface.GridMap;
import warehouseInterface.JobTable;
import warehouseInterface.RobotTable;
import warehouseInterface.Statistics;

/*
 * @author Maria
 * A class that sends commands one by one to the robot and waits for execution
 */

/**
 * @author maria
 *
 */
/**
 * @author maria
 *
 */
public class RouteExecution extends Thread {

	private boolean running = true;
	private int nrOfRobots = 0;
	private PathFinding pathfinder;
	private PriorityQueue<Job> priorityQueue;
	

	public RouteExecution(WarehouseMap map) {
		this.nrOfRobots = AllRobots.getAllRobots().size();
		this.pathfinder = new PathFinding(map);
		this.priorityQueue = Selection.createQueue();
	}

	/**
	 * Main method that sends commands and checks the status of the robots
	 */

	public void run() {

		boolean done = true;
		initVariables();
		while (running) {
			
			//check number of robots in case new robots have been added
			if (this.nrOfRobots < AllRobots.getAllRobots().size()) {
				for (int j = this.nrOfRobots; j < AllRobots.getAllRobots().size(); j++) {
					pathfinder.addRobot(AllRobots.getAllRobots().get(j));

				}
				this.nrOfRobots = AllRobots.getAllRobots().size();
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			
			// check if all the robots are doing jobs and assign them jobs if not
			for (int ir = 0; ir < nrOfRobots; ir++) {
				
				String name = this.getRobotName(ir);
				if (isFunctioning(name)) {
					if (AllRobots.getRobot(name).isDoingJob) {
						Job job = this.getJob();

						this.assignJob(name, job);
					}
				}
			}
			
			// check if all the robots have a task they are doing or not and assign tasks to them
			for (int ir = 0; ir < nrOfRobots; ir++) {				
				String name = this.getRobotName(ir);
				if (isFunctioning(name)) {
					if ((!AllRobots.getRobot(name).hasATask) && (!AllRobots.getRobot(name).droppingOff)) {
						//check if robots should go to the drop off point
						if (AllRobots.getRobot(name).currTaskIndex >= AllRobots.getRobot(name).currJob.getNumOfTasks()) {
							// robot should go to drop off

							Vector<Direction> path = pathfinder.GetPath(this.getRobotLocation(name),
									AllRobots.getRobot(name).currJob.dropOff, AllRobots.getRobot(name));
							System.out.println("assigning task to drop off:" + path);
							if (path != null) {
								AllRobots.getRobot(name).originalGoal = AllRobots.getRobot(name).currJob.dropOff;
								AllRobots.getRobot(name).goingToDropOff = true;
								this.assignTask(path, name);
								JobTable.updateStatus(AllRobots.getRobot(name).currJob.getJobID(),
										"Moving to drop-off point");
							}
						} else {

							Vector<Direction> task = this.getNextTask(name);
							
							if (task != null) {
								AllRobots.getRobot(name).originalGoal = AllRobots.getRobot(name).currJob
										.getTaskAtIndex(AllRobots.getRobot(name).currTaskIndex).get().getLocation();
								this.assignTask(task, name);
								System.out.println("assigning task2:" + task);
							} else {
								System.out.println(AllRobots.getRobot(name).currJob);
							}
						}
					}
				}
			}

			// now that we know all robots have jobs and tasks we can send commands
			for (int ir = 0; ir < nrOfRobots; ir++) {
				String name = this.getRobotName(ir);

				if (isFunctioning(name)) {
					// check if robot reached destination
					if (AllRobots.getRobot(name).goingToDropOff) {
						if (AllRobots.getRobot(name).currJob.dropOff.equals(this.getRobotLocation(name))) {
							// reached drop off
							if (!AllRobots.getRobot(name).droppingOff) {
								AllRobots.getRobot(name).setStopped();
								AllRobots.getRobot(name).droppingOff = true;

								AllPuppets.send(name, new DropOffPoint((int) AllRobots.getRobot(name).currJob.dropOff.getX(),(int) AllRobots.getRobot(name).currJob.dropOff.getY()));
							}
						} else {
							//robot has not reached drop off but is heading towards it
							if (this.getTaskMoveIndex(name) < this.getCurrentTask(name).size()) {
								//robot still has moves to execute
								if (AllRobots.getRobot(name).getStopped(GlobalClock.getCurrentTime()) < 0) {
									Direction facingDir = this.getRobotFacingDirection(name);
									Direction moveDir = this.getCurrentTask(name).get(this.getTaskMoveIndex(name));

									Move nextmove = this.getMove(facingDir, moveDir, name);
									if (nextmove == null)
										System.out.println("error generating the move object");
									else {
										// send the move command to the robot
										AllRobots.getRobot(name).nextRobotLocation = nextmove.getNextLocation();

										AllRobots.getRobot(name).nextDir = this.getCurrentTask(name)
												.get(this.getTaskMoveIndex(name));

										AllPuppets.send(name, nextmove);

										AllRobots.getRobot(name).waitingForMoveReport = true;
										done = false;
										System.out.println("sent next move: " + nextmove.toString());
										RobotTable.updateStatus(name, "Moving to " + nextmove.getNextLocation().x + ", "
												+ nextmove.getNextLocation().y);

									}
								}
							} else {
								//robot ran out of moves and hasn't reached the drop off which means it's queued to avoid collision with other robots
								
								if (this.getTaskMoveIndex(name) >= this.getCurrentTask(name).size()) {
									//request a new path to the drop off
									Vector<Direction> task = pathfinder.GetPath(this.getRobotLocation(name),
											AllRobots.getRobot(name).currJob.dropOff, AllRobots.getRobot(name));
									
									System.out.println("assigning task to drop off:" + task);
									
									if (task != null) {
										this.assignTask(task, name);
										
									}
								}

							}
						}
					} else {
						//robot is heading towards an item
						if (this.getItemLocation(AllRobots.getRobot(name).currTaskIndex, name).equals(this.getRobotLocation(name))) {
							//robot reached item
							if (!AllRobots.getRobot(name).pickingUp && !AllRobots.getRobot(name).droppingOff) {
								AllRobots.getRobot(name).setStopped();
								AllRobots.getRobot(name).pickingUp = true;
								AllPuppets.send(name, getTask(name));
								
							}

						} else { 
							//robot has not reached item yet
							if (!AllRobots.getRobot(name).waitingForMoveReport && AllRobots.getRobot(name).hasATask)

							{
								Direction facingDir = this.getRobotFacingDirection(name);
								System.out.println(name + ": " + this.getTaskMoveIndex(name) + "; " + this.getCurrentTask(name).size());
								if (this.getTaskMoveIndex(name) >= this.getCurrentTask(name).size()) {
									
									//robot has no more moves to execute which means it is in a queue
									//request a new path to the item
									Vector<Direction> task = this.getNextTask(name);
									
									if (task != null) {
										this.assignTask(task, name);
									}

								} else {
									
									if (AllRobots.getRobot(name).getStopped(GlobalClock.getCurrentTime()) < 0) {
										
										Direction moveDir = this.getCurrentTask(name).get(this.getTaskMoveIndex(name));

										Move nextmove = this.getMove(facingDir, moveDir, name);
										if (nextmove == null)
											System.out.println("error generating the move object");
										else {
											// send the move command to the robot
											AllRobots.getRobot(name).nextRobotLocation = nextmove.getNextLocation();

											AllRobots.getRobot(name).nextDir = this.getCurrentTask(name)
													.get(this.getTaskMoveIndex(name));

											AllPuppets.send(name, nextmove);

											AllRobots.getRobot(name).waitingForMoveReport = true;
											done = false;
											
											RobotTable.updateStatus(name, "Moving to " + nextmove.getNextLocation().x
													+ ", " + nextmove.getNextLocation().y);
										}
									}
								}
							}
						}
					}
				}
			}

			// commands have been sent now to wait for move reports

			boolean timepassed = false;
			while (!done && running) {
				done = true;
				for (int ir = 0; ir < nrOfRobots; ir++) {
					String name = this.getRobotName(ir);
					if (isFunctioning(name)) {
						if (AllRobots.getRobot(name).waitingForMoveReport) {

							if (AllRobots.getRobot(name).hasMoved) {
								AllRobots.getRobot(name).waitingForMoveReport = false;
								this.robotHasMoved(AllRobots.getRobot(name).nextRobotLocation, name,
										AllRobots.getRobot(name).nextDir);

								
								AllRobots.getRobot(name).currDirectionsIndex++;
								AllRobots.getRobot(name).hasMoved = false;
								
								timepassed = true;
							} else
								done = false;
						}
					}
				}

			}
			if (timepassed)	GlobalClock.increaseTime();
			

			// check if any of the robots have picked up their items
			for (int ir = 0; ir < nrOfRobots; ir++) {
				String name = this.getRobotName(ir);
				if (this.isFunctioning(name)) {
					if (AllRobots.getRobot(name).pickingUp) {
						if (AllRobots.getRobot(name).hasCompletedTask) {
							AllRobots.getRobot(name).pickingUp = false;

							AllRobots.getRobot(name).currTaskIndex++;
							AllRobots.getRobot(name).hasATask=false;
							
						}
					}
				}
			}

			

			// check if any of the robots have dropped the items at the drop off point
			for (int ir = 0; ir < nrOfRobots; ir++) {
				String name = this.getRobotName(ir);
				if (this.isFunctioning(name)) {
					if (AllRobots.getRobot(name).droppingOff) {
						if (AllRobots.getRobot(name).finishedDroppingItems) {
							// job complete!!
							this.increaseReward(name);
							
							JobTable.updateStatus(AllRobots.getRobot(name).currJob.getJobID(), "Completed");
							this.initVariables(name);
						}
					}
				}
			}

		}
	}

	/**
	 * A method that generates a Move object based on the direction the robot
	 * needs to go
	 * 
	 * @param facingDir
	 *            the direction the robot is facing
	 * @param moveDir
	 *            the direction the robot needs to move
	 * @return the Move object
	 */

	private Move getMove(Direction facingDir, Direction moveDir, String name) {
		Point newLoc = null;
		Objects.Sendable.Move mv = null;
		Point loc = getRobotLocation(name);
		switch (moveDir) {
		case NORTH:
			switch (facingDir) {
			case NORTH:
				newLoc = new Point((int) loc.getX(), (int) loc.getY() + 1);
				mv = new Objects.Sendable.Move('f', newLoc);
				break;

			case SOUTH:
				newLoc = new Point((int) loc.getX(), (int) loc.getY() + 1);
				mv = new Objects.Sendable.Move('b', newLoc);
				break;

			case EAST:
				newLoc = new Point((int) loc.getX(), (int) loc.getY() + 1);
				mv = new Objects.Sendable.Move('l', newLoc);
				break;

			case WEST:
				newLoc = new Point((int) loc.getX(), (int) loc.getY() + 1);
				mv = new Objects.Sendable.Move('r', newLoc);
				break;

			default:
				System.out.println("error - wrong value for robot facing direction");
			}
			break;

		case SOUTH:
			switch (facingDir) {
			case NORTH:
				newLoc = new Point((int) loc.getX(), (int) loc.getY() - 1);
				mv = new Objects.Sendable.Move('b', newLoc);
				break;

			case SOUTH:
				newLoc = new Point((int) loc.getX(), (int) loc.getY() - 1);
				mv = new Objects.Sendable.Move('f', newLoc);
				break;

			case EAST:
				newLoc = new Point((int) loc.getX(), (int) loc.getY() - 1);
				mv = new Objects.Sendable.Move('r', newLoc);
				break;

			case WEST:
				newLoc = new Point((int) loc.getX(), (int) loc.getY() - 1);
				mv = new Objects.Sendable.Move('l', newLoc);
				break;

			default:
				System.out.println("error - wrong value for robot facing direction");
			}
			break;

		case EAST:
			switch (facingDir) {
			case NORTH:
				newLoc = new Point((int) loc.getX() + 1, (int) loc.getY());
				mv = new Objects.Sendable.Move('r', newLoc);
				break;

			case SOUTH:
				newLoc = new Point((int) loc.getX() + 1, (int) loc.getY());
				mv = new Objects.Sendable.Move('l', newLoc);
				break;

			case EAST:
				newLoc = new Point((int) loc.getX() + 1, (int) loc.getY());
				mv = new Objects.Sendable.Move('f', newLoc);
				break;

			case WEST:
				newLoc = new Point((int) loc.getX() + 1, (int) loc.getY());
				mv = new Objects.Sendable.Move('b', newLoc);
				break;

			default:
				System.out.println("error - wrong value for robot facing direction");
			}
			break;

		case WEST:
			switch (facingDir) {
			case NORTH:
				newLoc = new Point((int) loc.getX() - 1, (int) loc.getY());
				mv = new Objects.Sendable.Move('l', newLoc);
				break;

			case SOUTH:
				newLoc = new Point((int) loc.getX() - 1, (int) loc.getY());
				mv = new Objects.Sendable.Move('r', newLoc);
				break;

			case EAST:
				newLoc = new Point((int) loc.getX() - 1, (int) loc.getY());
				mv = new Objects.Sendable.Move('b', newLoc);
				break;

			case WEST:
				newLoc = new Point((int) loc.getX() - 1, (int) loc.getY());
				mv = new Objects.Sendable.Move('f', newLoc);
				break;

			default:
				System.out.println("error - wrong value for robot facing direction");
			}
			break;

		default:
			System.out.println("error - wrong value for robot move");
		}

		return mv;

	}

	/**
	 * A method that tells the system interface that a job has been completed in order to increase the reward
	 * @param name the robot name
	 */
	private void increaseReward(String name) {
		Statistics.increaseRevenue(AllRobots.getRobot(name).currJob.getTotalReward());
		Statistics.jobDone();
	}

	/**
	 * Gets the task a robot is currently doing
	 * @param name the robot name
	 * @return the SingleTask object representing the task the robot is doing
	 */
	private SingleTask getTask(String name) {
		int index = AllRobots.getRobot(name).currTaskIndex;
		return AllRobots.getRobot(name).currJob.getTaskAtIndex(index).get();

	}

	
	/**
	 * A method that checks if a robot is functioning
	 * @param name the robot name
	 * @return true if it is functioning
	 */
	private boolean isFunctioning(String name) {
		return AllRobots.getRobot(name).functioning;
	}

	/**
	 * A method for initialising all robot variables
	 */
	private void initVariables() {
		for (int i = 0; i < this.nrOfRobots; i++) {
			String name = this.getRobotName(i);
			AllRobots.getRobot(name).isDoingJob = false;
			AllRobots.getRobot(name).pickingUp = false;
			AllRobots.getRobot(name).droppingOff = false;
			AllRobots.getRobot(name).hasATask = false;
			AllRobots.getRobot(name).finishedDroppingItems = false;
			AllRobots.getRobot(name).waitingForMoveReport = false;
			AllRobots.getRobot(name).hasMoved = false;
			AllRobots.getRobot(name).hasCompletedTask = false;
			AllRobots.getRobot(name).currDirectionsIndex = 0;
			AllRobots.getRobot(name).currTaskIndex = 0;
			AllRobots.getRobot(name).goingToDropOff = false;
		}

	}

	/** A method for initialising a single robot's variables
	 * @param name the robot's name
	 */
	public void initVariables(String name) {

		AllRobots.getRobot(name).isDoingJob = false;
		AllRobots.getRobot(name).pickingUp = false;
		AllRobots.getRobot(name).droppingOff = false;
		AllRobots.getRobot(name).hasATask = false;
		AllRobots.getRobot(name).finishedDroppingItems = false;
		AllRobots.getRobot(name).waitingForMoveReport = false;
		AllRobots.getRobot(name).hasMoved = false;
		AllRobots.getRobot(name).hasCompletedTask = false;
		AllRobots.getRobot(name).currDirectionsIndex = 0;
		AllRobots.getRobot(name).currTaskIndex = 0;
		AllRobots.getRobot(name).goingToDropOff = false;

	}

	


	/**
	 * A method that assigns a vector of directions to a robot
	 * @param task The vector of directions the robot needs to take 
	 * @param name the name of the robot
	 */
	private void assignTask(Vector<Direction> task, String name) {
		AllRobots.getRobot(name).hasATask=true;
		AllRobots.getRobot(name).SetUpPath(task, pathfinder.getTimePosReservations());

		AllRobots.getRobot(name).currDirectionsIndex = 0;
		AllRobots.getRobot(name).hasATask = true;
		AllRobots.getRobot(name).directions = (Vector<Direction>) task.clone();
	}


	

	/**
	 * A method that generates a vector of directions from the robot location to the next item location using route planning classes
	 * @param name the robot's name
	 * @return the vector of directions
	 */
	private Vector<Direction> getNextTask(String name) {
		if (AllRobots.getRobot(name).currTaskIndex >= AllRobots.getRobot(name).currJob.getNumOfTasks()) {
			return new Vector<Direction>();
		} else {
			SingleTask nextTask = AllRobots.getRobot(name).currJob.getTaskAtIndex(AllRobots.getRobot(name).currTaskIndex).get();
			if (nextTask != null) {
				Vector<Direction> path = pathfinder.GetPath(this.getRobotLocation(name), nextTask.getLocation(),
						AllRobots.getRobot(name));
				
				return path;

			} else {
				System.out.println("task null");
				return null;

			}
		}
	}


	/**
	 * A method that returns the vector of directions  robot is currently executing
	 * @param name the robot's name
	 * @return the vector of directions
	 */
	private Vector<Direction> getCurrentTask(String name) {
		return AllRobots.getRobot(name).directions;
	}

	/**
	 * A method that returns the index in the vector of directions a robot is currently at
	 * @param name the robot's name
	 * @return the index
	 */
	private int getTaskMoveIndex(String name) { 
		return AllRobots.getRobot(name).currDirectionsIndex;
	}

	

	/**
	 * A method that assigns a new job to a robot
	 * @param name the robot's name
	 * @param job the job
	 */
	private void assignJob(String name, Job job) {
		
		AllRobots.getRobot(name).currJob = job;
		AllRobots.getRobot(name).isDoingJob = true;
		JobTable.addJob(job.getJobID(), String.format("%.2f", job.getTotalReward()), name);
	}

	/**
	 * A method that gets the next job from the priority queue of jobs and removes it afterwards
	 * @return the job
	 */
	private Job getJob() {
		return priorityQueue.remove();
	}

	/**
	 * A method that can stop the thread's infinite while loops
	 */
	public void stopThread() {
		running = false;
	}

	/**
	 * A method that gets the robot name based on its index
	 * @param index the index of the robot 
	 * @return The name of the robot
	 */
	private String getRobotName(int index) {
		while (AllRobots.getAllRobots().isEmpty()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return AllRobots.getAllRobots().get(index).getName();
	}

	/**
	 * A method that gets the location of the item the robot is currently going to
	 * @param index The index of the task the robot is at
	 * @param name The robot's name
	 * @return The item's location
	 */
	private Point getItemLocation(int index, String name) {

		return AllRobots.getRobot(name).currJob.getTaskAtIndex(index).get().getLocation();
	}

	/**
	 * A method that modifies the robot location and refreshes the interface
	 * @param newLoc The new location of the robot
	 * @param name The robot's name
	 * @param newDir The new facing direction of the robot
	 */
	private void robotHasMoved(Point newLoc, String name, Direction newDir) {

		AllRobots.getRobot(name).setPosition(newLoc);
		AllRobots.getRobot(name).setDirection(newDir);
		GridMap.refresh();
	}

	/**
	 * A method the returns the current location of a robot
	 * @param name The robot's name
	 * @return The location
	 */
	private Point getRobotLocation(String name) {

		return AllRobots.getRobot(name).getPosition();
	}
	
	/**
	 * A method the returns the current facing direction of a robot
	 * @param name The robot's name
	 * @return The facing direction
	 */
	private Direction getRobotFacingDirection(String name) {

		return AllRobots.getRobot(name).getDirection();
	}

	/**
	 * A method used to receive move reports from the robot
	 * @param name The robot's name
	 * @param comm The move report
	 */
	public void addMoveReport(String name, MoveReport comm) {
		System.out.println("i have a move: " + comm);
		AllRobots.getRobot(name).hasMoved = comm.hasMoved();

	}

	/**
	 * A method used to receive complete reports from the robot
	 * @param name The robot's name
	 * @param comm The complete report
	 */
	public void addCompleteReport(String name, CompleteReport comm) {
		if (comm.wasCancelled()) {
			JobTable.updateStatus(AllRobots.getRobot(name).currJob.getJobID(), "Cancelled");
			JobProcessor.getJob(AllRobots.getRobot(name).currJob.getJobID()).cancel();
			Statistics.jobCancelled();
			initVariables(name);
			return;
		}
		if (comm.getIsPickup()) {
			if (comm.wasCompleted()) {
				AllRobots.getRobot(name).hasCompletedTask = true;
			} else {
				AllRobots.getRobot(name).hasATask = false;
				AllRobots.getRobot(name).pickingUp = false;
			}
		} else {
			if (comm.wasCompleted()) {
				AllRobots.getRobot(name).finishedDroppingItems = true;
			} else {
				AllRobots.getRobot(name).hasATask = false;
				AllRobots.getRobot(name).droppingOff = false;
			}
		}
	}

}
