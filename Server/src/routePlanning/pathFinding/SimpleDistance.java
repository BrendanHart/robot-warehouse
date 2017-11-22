package routePlanning.pathFinding;

import java.awt.Point;
import java.util.Vector;

import Objects.WarehouseMap;
import routePlanning.dataStructures.CameFrom;
import routePlanning.dataStructures.RobotsReservations;

/**
 * Returns simple distance between nodes
 * This class contains redundant code and should not exist in the final release!
 * @author Szymon
 *
 */
public class SimpleDistance {
	/**
	 * Node map
	 */
	private WarehouseMap map;
	
	private Point current;//Current node
	
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
	
	private Vector<Point> nodePath = new Vector<Point>(0);
	
	/**
	 * Only set map once and keep for the duration of the program
	 * @param map
	 */
	public SimpleDistance(WarehouseMap map) {
		this.map = map;
	}

	/**
	 * Returns path length from startNode to goalNode which avoids collisions with walls NOT robots
	 * @param startNode
	 * @param goalNode
	 * @param time current time
	 * @param robot
	 * @return
	 */
	public Integer GetDistnace(Point startNode, Point goalNode) {
		SetUp(startNode, goalNode);//Set up & clean up after potential previous search
		
		while(!frontier.isEmpty()) {
			current = GetBestNode();
			
			if(current.equals(goalNode))
				return ReconstructPath(goalNode);
			
			int currentGCost = GetGCost(current);
			RemoveFromFrontier(current);
			explored.addElement(current);
			
			Point neighbourNode = null;
			for(int i = 0; i < 4; i++){//For each neighbour node (at most 4) 
				
				switch (i) {
				case 0:
					neighbourNode = map.getAboveNode(current);
					break;
				case 1:
					neighbourNode = map.getBelowNode(current);	
					break;
				case 2:
					neighbourNode = map.getLeftNode(current);
					break;
				case 3:
					neighbourNode = map.getRightNode(current);
					break;
				}
				
				// Null - Tried to find neighbour out of bounds vvv
				if(neighbourNode == null || explored.contains(neighbourNode) 
					|| map.isObstacle(neighbourNode) || RobotsReservations.IsReserved(neighbourNode, currentGCost + 1)){
					continue;
				}
				
				fCost = GetFCost(currentGCost, current, goalNode);
				
				if(!frontier.contains(neighbourNode)) {
					//AddToFrontier(neighbourNode, currentGCost + 1, GetFCost(neighbourNode, goalNode));
					//Doesnt work with above, I suspect the reason being is: it saves a copy of neighbourNode when using AddToFrontier
					frontier.add(neighbourNode);
					gCosts.add(currentGCost + 1);
					fCosts.add(GetFCost(neighbourNode, goalNode));
				}
				else if (fCost >= GetFCost(neighbourNode, goalNode)){
					continue;
				}
				AddToPathTrace(current, neighbourNode);
			}
		}
		// Return Failure
		return null;
	}
	
	/**
	 * SetUp before calling GetPath
	 * @param startNode The start node
	 * @param goalNode The goal node
	 */
	private void SetUp(Point startNode, Point goalNode){
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
	 * @param node The node
	 * @return The GCost
	 */
	private int GetGCost(Point node){
		return gCosts.get(frontier.indexOf(node));
	}
	
	/**
	 * Heuristic returning the path cost from given to goal node
	 * @param fromNode The start node
	 * @param goalNode The goal node
	 * @return HCost
	 */
	private int GetHCost(Point fromNode, Point goalNode){
		return Math.abs(goalNode.x - fromNode.x) + Math.abs(goalNode.y - fromNode.y);
	}
	
	private int GetFCost(Point node, Point goalNode){
		return GetGCost(node) + GetHCost(node, goalNode);
	}
	
	private int GetFCost(int gCost, Point node, Point goalNode){
		return gCost + GetHCost(node, goalNode);
	}
	
	/**
	 * Return the best node in the frontier according to fCost value
	 * @return The point location of the best node
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
	 * Add a note to the frontier
	 * @param node The node to add (Point)
	 * @param gCost The gCost
	 * @param fCost The fCost
	 */
	private void AddToFrontier(Point node, int gCost, int fCost){
		frontier.add(node);
		gCosts.add(gCost);
		fCosts.add(fCost);
	}
	
	/**
	 * Remove a node from the frontier
	 * @param node The node to remove (Point)
	 */
	private void RemoveFromFrontier(Point node){
		int nodeIndex = frontier.indexOf(node);
		
		gCosts.remove(nodeIndex);
		fCosts.remove(nodeIndex);
		frontier.remove(nodeIndex);
	}
	
	/**
	 * Add to the path track
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
	 * Returns path length(distance)
	 * @param goalNode
	 * @param robot 
	 * @return set of directions to the goal
	 */
	private Integer ReconstructPath(Point goalNode) {	
		int pathLength = 1;
		//Look for goal node i.e. a starting point to trace the solution path
		for (int i = 0; i < pathTrace.size(); i++){
			Point toNode = pathTrace.get(i).toNode;
			if (goalNode.equals(toNode)){//Found path trace end
				
				Point fromNode = pathTrace.get(i).fromNode;
				
				boolean foundPrevNode;
				
				do{
					foundPrevNode = false;
					
					for (int j = 0; j < pathTrace.size() - 1; j++){//Goal node at the end therefore dont need to check it again hence -1
						toNode = pathTrace.get(j).toNode;
						if (fromNode.equals(toNode)){//Found prev node container
							fromNode = pathTrace.get(j).fromNode;
							foundPrevNode = true;
							pathLength++;
						}
					}
					
				}
				while(foundPrevNode);
				
				return pathLength;
			}
		}
		return null;
	}
}