package routePlanning.pathFinding;

import java.awt.Point;
import java.util.Vector;

import Objects.Direction;
import Objects.GlobalClock;
import Objects.WarehouseMap;
import Objects.Sendable.RobotInfo;
import routePlanning.dataStructures.CameFrom;
import routePlanning.dataStructures.RobotsReservations;
import routePlanning.dataStructures.TimePosReservations;

/**
 * Returns an optimal path from a start node to the goal node considering other robots' mevements i.e. avoids collisions with both walls & other robots
 * @author Szymon
 * As an extension I could make the algorithm tell the robot to wait for a mobile robot to pass instead of moving around the mobile robot to minimise travel distance
 * e.g. in the case Robot0 1,0 -> 1,2 Robot1 0,1 -> 2,1
 */

public class PathFinding {
	Point queuePos = null;
	/**
	 * Node map
	 */
	private WarehouseMap map;

	private Point currentExplored;//Current node

	/**
	 * Explored node set
	 */
	private Vector<Point> explored = new Vector<Point>(0);

	/**
	 * Frontier node set
	 */
	private Vector<Point> frontier = new Vector<Point>(0);

	/**
	 * PathTrace node set
	 */
	private Vector<CameFrom> pathTrace = new Vector<CameFrom>(0);

	/**
	 * gCosts(cost of journey from start to current node) for explored & frontier nodes
	 * 
	 * Each frontier node has the time at which the robot could be at that node
	 * this time is equal to the gCost of the frontier node
	 */
	private int gCost = Integer.MAX_VALUE;
	private Vector<Integer> gCosts = new Vector<Integer>(0);

	/**
	 * fCosts(gCost + hCost()) for explored & frontier nodes
	 */
	private int fCost = Integer.MAX_VALUE;
	private Vector<Integer> fCosts = new Vector<Integer>(0);

	///////////////////////////////////////////////////////////////////Time & Pos Reservations///////////////////////////////////////////////////////////////////////////
	TimePosReservations timePosReservations;//Reset every time a new path is generated

	private Vector<Point> nodePath = new Vector<Point>(0);

	/**
	 * Only set map once and keep for the duration of the program
	 * @param map
	 */
	public PathFinding(WarehouseMap map) {
		this.map = map;
	}

	/**
	 * Add a new robot i.e. an obstacle that needs to be considered
	 * @param robot
	 */
	public void addRobot(RobotInfo robot){
		RobotsReservations.AddRobot(robot);
	}

	/**
	 * Get the time-position reservations for a robot instance after getting the path
	 * @return
	 */
	public TimePosReservations getTimePosReservations() {
		return timePosReservations;
	}

	/**
	 * Returns a path from startNode to goalNode which avoids collisions and registers the returned path data so that other robots will not interfere in the future
	 * @param startNode
	 * @param goalNode
	 * @param time current time
	 * @param robot
	 * @return
	 */
	/*
	 * When called the current time is set
	 */
	public Vector<Direction> GetPath(Point startNode, Point goalNode, RobotInfo robot) {
		if(startNode.equals(goalNode))
			return null;//Already at destination

		int greatestReservedTime = RobotsReservations.getGreatestReservedTime();

		//Temp most optimal path data
		Vector<Direction> bestPathDirections = new Vector<Direction>(0);
		TimePosReservations bestTimePosReservations = null;
		queuePos = null;
		return findOptimalPath(startNode, goalNode, GlobalClock.getCurrentTime(), robot, bestPathDirections, bestTimePosReservations, greatestReservedTime, true);
	}

	/**
	 * Get the queue position
	 * @return The queue position
	 */
	public Point getQueuePos() {
		return queuePos;
	}

	/**
	 * Recursive function calculating the optimal path for the given robot in the current situation (WHCA*) i.e. iterates over all time windows + 1 and returns the shortest path (may wait in its current
	 * position for other robots to move out of the way)
	 * @param startNode
	 * @param goalNode
	 * @param time time at which the path is being calculated
	 * @param robot
	 * @param bestPathDirections
	 * @param bestTimePosReservations
	 * @param greatestReservedTime
	 * @return
	 */
	private Vector<Direction> findOptimalPath(Point startNode, Point goalNode, int time, RobotInfo robot, Vector<Direction> bestPathDirections, TimePosReservations bestTimePosReservations, int greatestReservedTime, boolean callSafe) {
		SetUp(startNode, goalNode);//Set up & clean up after potential previous search

		while(!frontier.isEmpty()) {
			currentExplored = GetBestNode();

			//Note that in some scenarios e.g. in dead end if a robot is covering goal and moves out of the way later then the algorithm will not find the path to the goal
			//But it is possible in other cases for the robot to cover the goal in one time window then move out of the way and the algorithm will find the path
			//SOLVED: Because a new path is looked for in the next time window only if the current algorithm iteration finds a goal the dead end scenario may be an Achilles heel of this setup
			//NOW the algorithm will look at all time windows regardless of finding a path in any one
			if(currentExplored.equals(goalNode)){
				Vector<Direction> tempPathDirections = ReconstructPath(goalNode, robot, time);
				if(!(tempPathDirections == null) && !RobotsReservations.isReservedForStopping(goalNode, timePosReservations.getLastReservedTime()) && (tempPathDirections.size() < bestPathDirections.size() || bestPathDirections.size() == 0)){
					//System.out.println("isReservedForStopping : " + goalNode + " time " + timePosReservations.getLastReservedTime() + " " + RobotsReservations.isReservedForStopping(goalNode, timePosReservations.getLastReservedTime()));
					bestPathDirections = tempPathDirections;
					bestTimePosReservations = timePosReservations;//Best = current
				}
				break;
			}

			int currentGCost = GetGCost(currentExplored);
			RemoveFromFrontier(currentExplored);
			explored.addElement(currentExplored);

			Point neighbourNode = null;
			for(int i = 0; i < 4; i++){//For each neighbour node (at most 4) 

				switch (i) {
				case 0:
					neighbourNode = map.getAboveNode(currentExplored);
					break;
				case 1:
					neighbourNode = map.getBelowNode(currentExplored);	
					break;
				case 2:
					neighbourNode = map.getLeftNode(currentExplored);
					break;
				case 3:
					neighbourNode = map.getRightNode(currentExplored);
					break;
				}

				//Moving out of the way of another path set previously
				if(RobotsReservations.IsReserved(robot, robot.getPosition(), time + currentGCost)){
					if( bestPathDirections.size() > 0){
						greatestReservedTime = 0;//do not look into further time windows and return the best path so far
						break;
					}
					if(callSafe)
						return moveToSafePlace(time + currentGCost, robot);
					return null;
				}

				//goal node found and is occupied therefore stay in the node before it (gather round)
				if(neighbourNode != null && neighbourNode.equals(goalNode) && RobotsReservations.IsReserved(neighbourNode, time + currentGCost + 1)){
					goalNode = currentExplored;
					explored.remove(currentExplored);
					AddToFrontier(currentExplored, currentGCost, currentGCost);//Add currentExplored (goal) back to frontier
					queuePos = currentExplored;
					break;//This is potetially not an optimal choice of best path (end of path yet no time for major debugging)
				}

				//null - Tried to find neighbour out of bounds vvv
				else if(neighbourNode == null 
						|| explored.contains(neighbourNode) 
						|| map.isObstacle(neighbourNode) 
						|| RobotsReservations.IsReserved(neighbourNode, time + currentGCost + 1)
						|| RobotsReservations.IsReserved(neighbourNode, time + currentGCost) && RobotsReservations.IsReserved(currentExplored, time + currentGCost + 1)//Prevent robots switching places as the nodes in 
						//front of them are not reserved when thet meet head on
						//reserved yet this leads to collisions as robots move through each other
						|| RobotsReservations.IsReserved(robot, currentExplored, time + currentGCost)//has another robot reserved my current node while Im still here
						//prevent robots from waiting for other robots to move into them and then checking with success the free node the other robot left behind
						){
					continue;
				}

				fCost = GetFCost(currentGCost, currentExplored, goalNode);

				if(!frontier.contains(neighbourNode)){
					//AddToFrontier(neighbourNode, currentGCost + 1, GetFCost(neighbourNode, goalNode));
					//Doesnt work with above, I suspect the reason being is: it saves a copy of neighbourNode when using AddToFrontier
					frontier.add(neighbourNode);
					gCosts.add(currentGCost + 1);
					fCosts.add(GetFCost(neighbourNode, goalNode));
				}
				else if (fCost >= GetFCost(neighbourNode, goalNode)){
					continue;
				}

				AddToPathTrace(currentExplored, neighbourNode);
			}

		}

		if(time + 1 <= greatestReservedTime + 1){//Still time windows left to check
			//Could potentially make this a non recursive function to avoid potential stack overflow
			return findOptimalPath(startNode, goalNode, time + 1, robot, bestPathDirections, bestTimePosReservations, greatestReservedTime, false);
		}
		else if(bestPathDirections.size() > 0){//time > greatestReservedTime + 1 and at least 1 solution
			timePosReservations = bestTimePosReservations;//For getting separately after getting route
			return bestPathDirections;//Return best(shotest) path
		}

		//No paths to goal found e.g. goal was inside a wall
		return null;//Return Failure
	}

	/**
	 * Finds a path to the closest safe place if one exists, returns null otherwise
	 * @param startNode
	 * @param fromTime
	 * @param deadlineTime
	 * @param time
	 * @param robot
	 * @return
	 */
	private Vector<Direction> moveToSafePlace(int deadlineTime, RobotInfo robot) {
		deadlineTime -= 2;//Because passing into findOptimalPath as the last reserved time and the way findOptimalPath deals with this restriction

		int fromTime = GlobalClock.getCurrentTime();

		//Temp most optimal path data
		Vector<Direction> bestPathDirections = new Vector<Direction>(0);
		TimePosReservations bestTimePosReservations = null;
		Vector<Direction> tempPath = null;
		int gridWidth = map.getGridHeight();
		int gridHeight = map.getGridHeight();
		for (int t = fromTime; t < deadlineTime; t++){//For each time window before the deadline
			for (int x = 0; x < gridWidth; x++){//For each node
				for (int y = 0; y < gridHeight; y++){
					System.out.println(t + " " + x + " " + y);

					tempPath = findOptimalPath(robot.getPosition(), new Point(x, y), t, robot, bestPathDirections, bestTimePosReservations, deadlineTime, false);//Find the shortest path to any safe place

					//Commented out assumuning the bestPathDirections & bestTimePosReservations are passed in by ref to findOptimalPath and changed there
					if(tempPath != null) {//The following is checked in findOptimalPath: && (bestPathDirections.size() == 0 || tempPath.size() < bestPathDirections.size())){
						bestPathDirections = tempPath;
						bestTimePosReservations = timePosReservations;
					}
				}
			}
		}

		if(bestPathDirections.size() > 0)
			return bestPathDirections;
		if(robot.getID() == 5)
			System.out.println("Cannot find safe path for R5");
		return null;//No safePlace path found
	}

	/**
	 * SetUp before calling GetPath
	 * @param startNode
	 * @param goalNode
	 * @param time
	 */
	private void SetUp(Point startNode, Point goalNode){
		timePosReservations = new TimePosReservations();

		explored.clear();

		frontier.clear();

		pathTrace.clear();
		nodePath.clear();

		gCost = Integer.MAX_VALUE;
		gCosts.clear();

		fCost = Integer.MAX_VALUE;
		fCosts.clear();

		AddToFrontier(startNode, 0, GetFCost(0, startNode, goalNode));
	}

	/**
	 * Get the GCost 
	 * @param node The node to check
	 * @return The GCost
	 */
	private int GetGCost(Point node){
		return gCosts.get(frontier.indexOf(node));
	}

	/**
	 * Heuristic returning the path cost from given to goal node
	 * @param node
	 * @return
	 */
	private int GetHCost(Point fromNode, Point goalNode){
		return Math.abs(goalNode.x - fromNode.x) + Math.abs(goalNode.y - fromNode.y);
	}

	/**
	 * Get the FCost
	 * @param node The node
	 * @param goalNode The goal node
	 * @return The FCost
	 */
	private int GetFCost(Point node, Point goalNode){
		return GetGCost(node) + GetHCost(node, goalNode);
	}

	/**
	 * Get the FCost
	 * @param gCost The gCost
	 * @param node The node
	 * @param goalNode The goal node
	 * @return The FCost
	 */
	private int GetFCost(int gCost, Point node, Point goalNode){
		return gCost + GetHCost(node, goalNode);
	}

	/**
	 * Return the best node in the frontier according to fCost value
	 * @return
	 */
	private Point GetBestNode(){
		int bestNodeCost = Integer.MAX_VALUE;
		int bestNodeID = 0;

		for (int i = 0; i < frontier.size(); i++){
			if(fCosts.get(i) < bestNodeCost){
				bestNodeCost = fCosts.get(i);
				bestNodeID = i;
			}
		}

		return frontier.get(bestNodeID);
	}

	/**
	 * Add a node to the frontier
	 * @param node The node to add
	 * @param gCost The GCost
	 * @param fCost The FCost
	 */
	private void AddToFrontier(Point node, int gCost, int fCost){
		frontier.add(node);
		gCosts.add(gCost);
		fCosts.add(fCost);
	}

	/**
	 * Remove a node from the frontier
	 * @param node The node to remove
	 */
	private void RemoveFromFrontier(Point node){
		int nodeIndex = frontier.indexOf(node);

		gCosts.remove(nodeIndex);
		fCosts.remove(nodeIndex);
		frontier.remove(nodeIndex);
	}

	/**
	 * Add to path trace
	 * @param fromNode The start node
	 * @param toNode The goal node
	 */
	private void AddToPathTrace(Point fromNode, Point toNode) {
		for (int i = 0; i < pathTrace.size(); i++){
			if (toNode.equals(pathTrace.get(i).toNode)){//A worse path from node is linked, replace it with the given(better) fromNode
				pathTrace.get(i).fromNode = fromNode;
				return;
			}
		}
		pathTrace.add(new CameFrom(fromNode, toNode));
	}

	/**
	 * Returns a path as a set of directions relative to the North(Top of map)
	 * And creates reservations stored in the timePosReservations vector which need to be assigned to a robot
	 * @param goalNode
	 * @param robot
	 * @param time time at which path was calculated
	 * @return set of directions to the goal
	 */
	private Vector<Direction> ReconstructPath(Point goalNode, RobotInfo robot, int time) {
		Vector<Direction> directions = new Vector<Direction>(0);

		//Look for goal node i.e. a starting point to trace the solution path
		for (int i = 0; i < pathTrace.size(); i++){
			Point toNode = pathTrace.get(i).toNode;
			if (goalNode.equals(toNode)){//Found path trace end
				nodePath.insertElementAt(goalNode, 0);

				Point fromNode = pathTrace.get(i).fromNode;
				directions.insertElementAt(map.getRelativePosition(fromNode, toNode), 0);//insert last direction (not == orientation!) i.e. stack vector

				boolean foundPrevNode;

				do{
					foundPrevNode = false;

					for (int j = 0; j < pathTrace.size() - 1; j++){//Goal node at the end therefore dont need to check it again hence -1
						toNode = pathTrace.get(j).toNode;
						if (fromNode.equals(toNode)){//Found prev node container
							nodePath.insertElementAt(fromNode, 0);
							fromNode = pathTrace.get(j).fromNode;
							directions.insertElementAt(map.getRelativePosition(fromNode, toNode), 0);
							foundPrevNode = true;
						}
					}

				}
				while(foundPrevNode);

				//End of path trace
				int d = 0;
				for (; d < nodePath.size(); d++){
					timePosReservations.addReservation(time + d + 1, nodePath.get(d));
				}
				timePosReservations.setGreatestReservedTime(time + d);//Dont need to add 1 since at the end of the loop d is one more than the condition
				timePosReservations.setFirstReservedTime(time + 1);
				//System.out.println("R" + robot.getID() + " has reserved last node " + nodePath.get(d - 1) + " at time " + (time + d));
				assert(goalNode.equals(nodePath.get(d - 1)));
				//System.out.println("isReservedForStopping : " + nodePath.get(d - 1) + " time " + (time + d) + " " + RobotsReservations.isReservedForStopping(nodePath.get(d - 1), time + d));
				return directions;
			}
		}
		//System.out.println("Goal not found in path trace \nPath trace: " + pathTrace + "\n goal node: " + goalNode + "\nrobot(that wants to get there): " + robot.getID());
		return null;
	}
}





/*
function A*(start,goal)
ClosedSet := {}    	  // The set of nodes already evaluated.
OpenSet := {start}    // The set of tentative nodes to be evaluated, initially containing the start node
Came_From := the empty map    // The map of navigated nodes.

g_score := map with default value of Infinity
g_score[start] := 0    // Cost from start along best known path.
// Estimated total cost from start to goal through y.
f_score := map with default value of Infinity
f_score[start] := heuristic_cost_estimate(start, goal)

while OpenSet is not empty
    current := the node in OpenSet having the lowest f_score[] value
    if current = goal
        return reconstruct_path(Came_From, goal)

    OpenSet.Remove(current)
    ClosedSet.Add(current)
    for each neighbor of current
        if neighbor in ClosedSet
            continue		// Ignore the neighbor which is already evaluated.
        tentative_g_score := g_score[current] + dist_between(current,neighbor) // length of this path.
        if neighbor not in OpenSet	// Discover a new node
            OpenSet.Add(neighbor)
        else if tentative_g_score >= g_score[neighbor]
            continue		// This is not a better path.

        // This path is the best until now. Record it!
        Came_From[neighbor] := current
        g_score[neighbor] := tentative_g_score
        f_score[neighbor] := g_score[neighbor] + heuristic_cost_estimate(neighbor, goal)

return failure

function reconstruct_path(Came_From,current)
total_path := [current]
while current in Came_From.Keys:
    current := Came_From[current]
    total_path.append(current)
return total_path*/