package main;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import Objects.AllPuppets;
import Objects.AllRobots;
import Objects.Job;
import Objects.WarehouseMap;
import Objects.Sendable.CompleteReport;
import Objects.Sendable.MoveReport;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SendableObject;
import jobInput.JobProcessor;
import jobSelection.Probability;
import networking.Puppet;
import routeExecution.RouteExecution;
import warehouseInterface.GridMap;
import warehouseInterface.JobTable;
import warehouseInterface.RobotTable;
import warehouseInterface.Statistics;

/**
 * The main class to run all the server side code -- Merged with Server.java and
 * RobotLobby.java to save difficulties with communicating amongst the other
 * server sections.
 */
public class RunServer extends Thread {

	public static WarehouseMap map = new WarehouseMap("res/drops.csv");
	private RouteExecution routeExec;
	private boolean alive = true;

	public RunServer() {

		//// JobSelection (Fran & Brendan) -- Process the items
		JobProcessor.processItemFiles("res/items.csv", "res/locations.csv");
		JobProcessor.processJobFiles("res/training_jobs.csv", "res/jobs.csv", "res/cancellations.csv");
		Probability pCalculator = new Probability(JobProcessor.getAllTrainingJobs().values(), JobProcessor.getAllItems().values());
		for(Job j : JobProcessor.getAllJobs().values()) {
			j.setCancellationProb(pCalculator.probabilityCancelled(j));
		}

		// Creating puppets (connections to each robot)
		Puppet tay = new Puppet("TayTay", "0016531AF6E5");
		AllPuppets.addPuppet(tay);

		Puppet alfonso = new Puppet("Alfonso", "00165308DA58");
		AllPuppets.addPuppet(alfonso);

		Puppet johnCena = new Puppet("John Cena", "00165308E5A7");
		AllPuppets.addPuppet(johnCena);

		//// Setting up the WarehouseInterface (Artur)
		setUpWarehouse();
	}

	/**
	 * Keeps the server running.
	 */
	@Override
	public void run() {
		while (alive) {
			checkCommands();
			try {
				// Sleeping to allow others access to AllRobots.
				Thread.sleep(400);
			} catch (InterruptedException e) {
				out("RunMe run() sleep failed");
			}
		}
	}

	/**
	 * Checks through all the puppets, to see if they've sent any commands. 
	 * If they have, then it executes them accordingly.
	 */
	private void checkCommands() {
		ArrayList<Puppet> puppets = AllPuppets.getPuppets();

		for (Puppet pup : puppets) {
			SendableObject comm = null;
			while ((comm = pup.popCommand()) != null) {
				if (comm instanceof MoveReport) {
					routeExec.addMoveReport(pup.name(), (MoveReport) comm);
					System.out.println("GOT REPORT: " + ((MoveReport) comm).toString());
				} else if (comm instanceof CompleteReport) {
					routeExec.addCompleteReport(pup.name(), (CompleteReport) comm);
				} else if (comm instanceof RobotInfo) {
					/*
					 * Will only be called when a robot has started up, and had
					 * it's input given to it by the operator.
					 */
					RobotInfo ri = (RobotInfo) comm;
					if (AllRobots.checkExists(ri.getName())) {
						AllRobots.modifyRobotLocation(ri.getName(), ri.getPosition(), ri.getDirection());
					} else {
						AllRobots.addRobot(ri);
					}
				}
			}
		}
	}

	/**
	 * Sets up Artur's warehouse.
	 */
	private void setUpWarehouse() {

		JFrame frame = new JFrame("Warehouse Interface - 1.1");
		frame.setLayout(new GridLayout(2, 2));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(800, 600);

		frame.add(JobTable.draw(routeExec));
		frame.add(GridMap.createGrid(map));
		frame.add(RobotTable.draw());
		frame.add(Statistics.draw());
		frame.setVisible(true);


		//// Start RoutePlanning & RouteExecution (Szymon & Maria)
		routeExec = new RouteExecution(map);
		routeExec.start();
	}

	/**
	 * Helper command to print out objects for debugging
	 * @param n Object to print
	 */
	private void out(Object n) {
		System.out.println("" + n);
	}

	/**
	 * Main method to run everything
	 * @param args
	 */
	public static void main(String[] args) {
		RunServer everything = new RunServer();
		everything.start();
	}
}
