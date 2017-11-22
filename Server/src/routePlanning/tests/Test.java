package routePlanning.tests;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.util.Vector;

import Objects.Direction;
import Objects.GlobalClock;
import Objects.WarehouseMap;
import Objects.Sendable.RobotInfo;
import routePlanning.dataStructures.Reservation;
import routePlanning.dataStructures.RobotsReservations;
import routePlanning.pathFinding.PathFinding;
import routePlanning.pathFinding.SimpleDistance;

import java.util.Random;

/** Contains my own tests and as a result guidance for usage of route planning
 * classes & methods
 * 
 * @author Szymon */
public class Test {
	private boolean travelledToTheEnd = true;//To prevent path assignment to the same destination multiple times throughout the journey causing robots to stop
	private int mapWidth = 10;
	private int mapHeight =10;
	private int noOfRobots = 10;
	private WarehouseMap map = new WarehouseMap(mapWidth, mapHeight);
	private List<Point> obstacles;
	private RobotInfo stationaryRobot;
	private Point stationaryRobotPoint;
	private Vector<RobotInfo> testRobots = new Vector<RobotInfo>(0);
	private PathFinding pathFinding;
	private Vector<Direction> pathSequence;
	private Random randomGenerator = new Random();
	
	private int timeToMakeNewPaths = 10;
	private int timeToMakeNewPaths2 = 10;

	public boolean newPathTimer() {
		if(timeToMakeNewPaths > 0){
			timeToMakeNewPaths--;
			return false;
		}
		else {
			timeToMakeNewPaths = 3;
			return true;
		}
	}
	
	public boolean newPathTimer2() {
		if(timeToMakeNewPaths2 > 0){
			timeToMakeNewPaths2--;
			return false;
		}
		else {
			timeToMakeNewPaths2 = 10;
			return true;
		}
	}
	
	public void DrawMap() {
		RobotInfo currentRobot;
		String[][] displayMap = new String[mapWidth][mapHeight];
		
		for(int x = 0; x < mapWidth; x++){
			for(int y = 0; y < mapHeight; y++){
				displayMap[x][y] = map.isObstacle(x, y) ? "      " : "  -   ";
			}
		}
		
		int tempX;
		int tempY;
		for (int z = 0; z < testRobots.size(); z++) {
			currentRobot = testRobots.get(z);
			
			if(currentRobot.getID() == 5){
				System.out.println("R5 next node " + currentRobot.goToNextNode());
				System.out.println("R5 first reserved time " + currentRobot.getFirstReservedTime());
				System.out.println("R5 last reserved time " + currentRobot.getLastReservedTime());
				System.out.println("Current time " + GlobalClock.getCurrentTime());
				System.out.println("R5 pathSeq " + currentRobot.getPathSequence());
			}
			else{
				currentRobot.goToNextNode();
			}
			
			tempX = (int) currentRobot.getPosition().getX();
			tempY = (int) currentRobot.getPosition().getY();
			//System.out.println("R" + currentRobot.getID() + "  " + tempX + ", " + tempY);
			displayMap[tempX][tempY] = "  R" + currentRobot.getID();
			displayMap[tempX][tempY] += currentRobot.getID() > 9 ? " " : "  ";
		}
		
		//After EVERY robot has moved are there any robots on one node NOTE it is permitted while they are moving!
		//NOTE this will not catch the bug where one robot waits for the second to go on him so the first one can go to the position left behind by the second
		for (int z = 0; z < testRobots.size(); z++) {
			currentRobot = testRobots.get(z);
			for (int z2 = 0; z2 < testRobots.size(); z2++) {
				if(!currentRobot.equals(testRobots.get(z2))){
					if(currentRobot.getPosition().equals(testRobots.get(z2).getPosition()) == true){
						for(int y = 0; y < mapHeight; y++){
							for(int x = 0; x < mapWidth; x++){
								System.out.print(displayMap[x][y]);
							}
							System.out.println();
							System.out.println();
						}
						System.out.println("R" + currentRobot.getID() + " went into R" + testRobots.get(z2).getID());
						assert(currentRobot.getPosition().equals(testRobots.get(z2).getPosition()) == false);
					}
				}
			}
		}
		
		for(int y = 0; y < mapHeight; y++){
			for(int x = 0; x < mapWidth; x++){
				System.out.print(displayMap[x][y]);
			}
			System.out.println();
			System.out.println();
		}
	}

	public void assignRandomPaths() {
		Point startNode = null;
		Point endNode = null;
		
		for (int z = 0; z < testRobots.size(); z++) {
			RobotInfo currentRobot = testRobots.get(z);
			
			startNode = new Point((int) currentRobot.getPosition().getX(),(int) currentRobot.getPosition().getY());

			do{
				endNode = new Point(randomGenerator.nextInt(mapWidth), randomGenerator.nextInt(mapHeight));
			}while(map.isObstacle(endNode));//Do not give paths with destination inside walls
			
			pathSequence = pathFinding.GetPath(startNode, endNode, currentRobot);
			
			if(pathSequence == null){//E.g. tried to get path from currentNode to currentNode
									//OR  tried to get path to reserved node in which a robot stands stationary for long and doesnt move out of the way
				continue;
			}
			
			currentRobot.SetUpPath(pathSequence, pathFinding.getTimePosReservations());
		}
	}
	
	public void assignRandomPaths(RobotInfo robot) {
		Point endNode = null;
		Point startNode = new Point((int) robot.getPosition().getX(),(int) robot.getPosition().getY());

		do{
			endNode = new Point(randomGenerator.nextInt(mapWidth), randomGenerator.nextInt(mapHeight));
		}while(map.isObstacle(endNode));//Do not give paths with destination inside walls
		
		pathSequence = pathFinding.GetPath(startNode, endNode, robot);
		
		if(pathSequence == null){//E.g. tried to get path from currentNode to currentNode
								//OR  tried to get path to reserved node in which a robot stands stationary for long and doesnt move out of the way
			return;
		}
		
		robot.SetUpPath(pathSequence, pathFinding.getTimePosReservations());
	}
	
	public void assignPathsToStart(){
		int goalX = 0;
		int goalY = 0;
		
		Point goalNode;
		RobotInfo tempRobot;
		
		for(int i = 0; i < noOfRobots; i++){
			tempRobot = testRobots.get(i);
			if(tempRobot.getID() < 5){
				goalX = (int) tempRobot.getPosition().getX();
				goalY = 0;
			}
			else{
				goalX = 0;
				goalY = (int) tempRobot.getPosition().getY();
			}
			
			goalNode = new Point(goalX, goalY);
			pathSequence = pathFinding.GetPath(tempRobot.getPosition(), goalNode, tempRobot);
			if(tempRobot.getID() == 5)
				System.out.println("R5 Assigning path to start" + pathSequence);
			tempRobot.SetUpPath(pathSequence, pathFinding.getTimePosReservations());
		}
	}
	
	public void assignPathsToEnd(){
		int goalX = 0;
		int goalY = 0;
		
		Point goalNode;
		RobotInfo tempRobot;
		
		for(int i = 0; i < noOfRobots; i++){
			tempRobot = testRobots.get(i);
			if(tempRobot.getID() < 5){
				goalX = (int) tempRobot.getPosition().getX();
				goalY = 9;
			}
			else{
				goalX = 9;
				goalY = (int) tempRobot.getPosition().getY();
			}
			
			goalNode = new Point(goalX, goalY);
			pathSequence = pathFinding.GetPath(tempRobot.getPosition(), goalNode, tempRobot);
			if(tempRobot.getID() == 5)
				System.out.println("R5 Assigning path to end" + pathSequence);
			tempRobot.SetUpPath(pathSequence, pathFinding.getTimePosReservations());
		}
	}
	
	public static void main(String[] args) {
		Test test = new Test();

		/*test.obstacles = new ArrayList<Point>();
		test.obstacles.add(new Point(6, 4));
		test.obstacles.add(new Point(5, 4));
		test.obstacles.add(new Point(4, 4));
		test.obstacles.add(new Point(3, 4));

		test.obstacles.add(new Point(7, 4));
		test.obstacles.add(new Point(7, 5));
		test.obstacles.add(new Point(7, 6));
		test.obstacles.add(new Point(7, 7));

		test.obstacles.add(new Point(6, 7));
		test.obstacles.add(new Point(5, 7));
		test.obstacles.add(new Point(4, 7));
		test.obstacles.add(new Point(3, 7));

		test.map.setObstacles(test.obstacles);*/

		/*RobotInfo robot0 = new RobotInfo(0, test.map);
		RobotInfo robot1 = new RobotInfo(1, test.map);
		test.stationaryRobot = new RobotInfo(2, test.map);
		test.testRobots.add(robot0);
		test.testRobots.add(robot1);
		test.testRobots.add(test.stationaryRobot);
*/
		test.pathFinding = new PathFinding(test.map);
		/*test.pathFinding.addRobot(robot0);
		test.pathFinding.addRobot(robot1);
		test.pathFinding.addRobot(test.stationaryRobot);

		test.stationaryRobot.SetUpStationary(new Point(0, 0));
		Point startNode = new Point(1, 0);
		Point endNode = new Point(1, 2);*/

		/*//Simple distance example
		SimpleDistance simpleDistance = new SimpleDistance(test.map);
		System.out.println("SimpleDistance No of steps: " + simpleDistance.GetDistnace(startNode, endNode));

		test.pathSequence = test.pathFinding.GetPath(startNode, endNode, robot0);
		robot0.SetUpPath(startNode, test.pathSequence, test.pathFinding.getTimePosReservations());
		
		startNode = new Point(0, 1);
		endNode = new Point(1, 2);

		test.pathSequence = test.pathFinding.GetPath(startNode, endNode, robot1);
		robot1.SetUpPath(startNode, test.pathSequence, test.pathFinding.getTimePosReservations());
		*/
		
		int startX = 0;
		int startY = 0;
		
		int goalX = 0;
		int goalY = 0;
		
		Point startNode = new Point(startX, startY);
		Point goalNode = new Point(2, 2);
		RobotInfo tempRobot = null;
		int robotID = 0;
		for(int i = 0; i < test.noOfRobots; i++){
			
			if(startX++ > test.mapWidth - 1){
				startX = 0;
			}
			
			if(tempRobot != null && tempRobot.getID() >= 4){
				startX = 0;
				startY++;
			}
			
			startNode = new Point(startX, startY);
			tempRobot = new RobotInfo(robotID++, test.map, startNode);
			
			test.testRobots.add(tempRobot);
			test.pathFinding.addRobot(tempRobot);
		}
		
		//Cannot assign paths to robots straight after creating each one
		
		/*
		 * Stationary robot
		 */
		startX = 9;
		startY = 9;
		
		startNode = new Point(startX, startY);
		tempRobot = new RobotInfo(robotID++, test.map, startNode);
		
		test.testRobots.add(tempRobot);
		test.pathFinding.addRobot(tempRobot);
		tempRobot.SetUpStationary(startNode);
		
		while (true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			////////////////////////////////////////////IMPORTANT/////////////////////////////////////////////////////////////////////////
			//IF ROBOT HAS NO RESERVED NEXT NODE THEN DO NOT MOVE HIM< HE MAY BE WAITING FOR OTHER ROBOTS TO PASS
			//CURRENTLY THERE IS A BUG IF A ROBOT IS SENT TO AN OCCUPIED LOCATION
			if(test.newPathTimer()){
				//test.assignRandomPaths();
			}
			
			if(test.testRobots.get(9).getPosition().getX() == test.mapWidth - 1 && !test.travelledToTheEnd){
				test.assignPathsToStart();
				test.travelledToTheEnd = true;
			}
			
			if(test.testRobots.get(9).getPosition().getX() == 0 && test.travelledToTheEnd && test.newPathTimer2()){
				test.assignPathsToEnd();
				test.travelledToTheEnd = false;
			}
			
			if(GlobalClock.getCurrentTime() == 5 || GlobalClock.getCurrentTime() == 30){
				//AT TIME == 30 A SITUATION ARISES WHERE R10 IS REDIRECTED WHILE ON ITS PATH TO A LOCATION IN FRONT OF MOVING ROBOTS, THE ROBOT DECIDES TO WAIT UNTIL ALL ROBOTS PASS
				//AND AT THE SAME TIME R3 ROBOT GOES INTO R10 SO THAT R10 HAS A FREE SPACE IT CAN NOW MOVE INTO I.E. THE SPACE OCCUPIED BY R3 BEFORE IT WENT INTO R10
				//I HAVE TO LOOK INTO CHANGED PATHS AND THE CONSEQUENCES ON CHECKED DATA
				
				//All robots have new paths computed:
				//R7 has completed its path and has its new path computed first so since R8 has old path at the time of this computation,R7 thinks it can move past him
				//R8 then computes new path but cannot move so it has to wait, it waits too long and R7 goes into R8!
				//* One way to solve this would be to screen shot the occupied positions at the time of new path computation and assume all robots will stay there so treat these positions as walls for
				//	that one computation XXX This approach leads to a dead lock
				//*	Another way would be moving the player out of the way to a safe place
				goalX = 3;
				goalY = 3;
				
				tempRobot = test.testRobots.get(10);
				
				goalNode = new Point(goalX, goalY);
				test.pathSequence = test.pathFinding.GetPath(tempRobot.getPosition(), goalNode, tempRobot);
				tempRobot.SetUpPath(test.pathSequence, test.pathFinding.getTimePosReservations());
			}
			
			if(GlobalClock.getCurrentTime() == 20){
				goalX = 9;
				goalY = 9;
				
				tempRobot = test.testRobots.get(10);
				
				goalNode = new Point(goalX, goalY);
				test.pathSequence = test.pathFinding.GetPath(tempRobot.getPosition(), goalNode, tempRobot);
				tempRobot.SetUpPath(test.pathSequence, test.pathFinding.getTimePosReservations());
			}
			
			if(GlobalClock.getCurrentTime() == 60){
				goalX = 1;
				goalY = 1;
				
				tempRobot = test.testRobots.get(10);
				
				goalNode = new Point(goalX, goalY);
				test.pathSequence = test.pathFinding.GetPath(tempRobot.getPosition(), goalNode, tempRobot);
				tempRobot.SetUpPath(test.pathSequence, test.pathFinding.getTimePosReservations());
			}
			
			test.DrawMap();

			GlobalClock.increaseTime();//All Robots moved >> increase the time by 1
			System.out.println("////////////////////////////////////////////////////////////////////////////////////////");
		}
	}

}
