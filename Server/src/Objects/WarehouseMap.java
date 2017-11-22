package Objects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Objects.Direction;
import jobInput.JobProcessor;
import rp.robotics.mapping.MapUtils;
import warehouseInterface.GridMap;

/**
 * SHARED OBJECTS
 * Represents the map of the warehouse
 */
public class WarehouseMap {

	// TRUE = OBSTACLE, FALSE = EMPTY
	private boolean[][] grid;
	private int gridWidth; 
	private int gridHeight;
	private List<Point> dropoffs;

	///////////////////////////////////////////////////////////////////////////////////Route Planning///////////////////////////////////////////////////////////////////////////////////
	private Point[][] map;

	/**
	 * Create a new warehouse map.
	 * @param doFile The file containing the drop off locations.
	 */ 
	public WarehouseMap(int gridWidth, int gridHeight) {//, String doFile) {

		// Get the size of th emap
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;

		// Create the grid (2D array of booleans)
		grid = new boolean[gridWidth][gridHeight];

		///////////////////////////////////////////////////////////////////////////////////Route Planning///////////////////////////////////////////////////////////////////////////////////
		//Set up & populate map
		map = new Point[gridWidth][gridHeight];	

		for (int i = 0; i < gridWidth; i++){
			for (int j = 0; j < gridHeight; j++){
				map[i][j] = new Point(i, j);
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Create a new warehouse map.
	 * @param doFile The file containing the drop off locations.
	 */
	public WarehouseMap(String doFile) {
		//GridMap nicksMap = GridMap.createRealWarehouse();

		//// Import the map created by Nick and set the obstacles
		rp.robotics.mapping.GridMap nicksMap = MapUtils.createMarkingWarehouseMap();

		// Get the size of the map & create it intnernally
		this.gridWidth = nicksMap.getXSize();
		this.gridHeight = nicksMap.getYSize();
		grid = new boolean[gridWidth][gridHeight];

		// Set obstacles in the grid
		for(int i = 0; i < gridWidth; i++) {
			for(int j = 0; j < gridHeight; j++) {
				grid[i][j] = nicksMap.isObstructed(i, j);
			}
		}

		// Read dropoff locations
		Optional<List<String>> dos = JobProcessor.readFile(doFile);

		// Set dropoff points
		if(!dos.isPresent()) {
			throw new IllegalArgumentException("Could not create dropoff points from the file.");
		}
		dropoffs = new ArrayList<Point>();
		for(String doPoints : dos.get()) {
			if(doPoints.equals("") || doPoints.equals(" "))
				continue;
			String[] doArr = doPoints.replaceAll("\\s","").split(",");
			Point p = new Point(Integer.parseInt(doArr[0]), Integer.parseInt(doArr[1]));
			dropoffs.add(p);
		}

		///////////////////////////////////////////////////////////////////////////////////Route Planning///////////////////////////////////////////////////////////////////////////////////
		//Set up & populate map
		map = new Point[gridWidth][gridHeight];	

		for (int i = 0; i < gridWidth; i++){
			for (int j = 0; j < gridHeight; j++){
				map[i][j] = new Point(i, j);
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}

	/**
	 * POTENTIALLY REDUNDANT METHOD
	 * Set the obstacles from a list of points.
	 * @param obstacles The list of obstacles.
	 */
	public void setObstacles(Point[] obstacles) {
		for(Point obstacle : obstacles)
			grid[(int) obstacle.getX()][(int) obstacle.getY()] = true;
	}

	/**
	 * Add a single obstacle to the warehouse.
	 * @param o The obstacle.
	 */
	public void addObstacle(Point o) {
		grid[(int) o.getX()][(int) o.getY()] = true;
	}

	/**
	 * Check if a given position is an obstacle.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return If the position is an obstacle or not.
	 */
	public boolean isObstacle(int x, int y) {
		return grid[x][y];
	}

	/**
	 * Check if a given position is an obstacle.
	 * @param p The point to check.
	 * @return If the position is an obstacle or not.
	 */
	public boolean isObstacle(Point p) {
		return grid[(int) p.getX()][(int) p.getY()];
	}

	/**
	 * Get the drop off points in the warehouse.
	 * @return The drop off locations.
	 */
	public List<Point> getDropoffPoints() {
		return dropoffs;
	}

	/**
	 * Calculate the distance to the wall at a given point and heading.
	 * @param p The position.
	 * @param heading The direction to check.
	 * @return The number of grid positions to a wall.
	 */
	public int distanceToWall(Point p, Direction heading) {
		int distance = 0;
		switch(heading) {        
		case NORTH:
			for(int i = (int) (p.getY()-1); i >= 0; i--) {
				if(!grid[(int) p.getX()][i])
					distance++;
				else
					break;
			}
			return distance;

		case SOUTH:
			for(int i = (int) (p.getY()+1); i < gridHeight; i++) {
				if(!grid[(int) p.getX()][i])
					distance++;
				else
					break;
			}
			return distance;

		case EAST:
			for(int i = (int) (p.getX()+1); i < gridWidth; i++) {
				if(!grid[i][(int) p.getY()])
					distance++;
				else
					break;
			}
			return distance;

		case WEST:
			for(int i = (int) (p.getX()-1); i >= 0; i--) {
				if(!grid[i][(int) p.getY()])
					distance++;
				else
					break;
			}
			return distance;

		default:
			return -1;
		}
	}

	/**
	 * Free a position (remove an obstacle).
	 * @param pos The position to free.
	 */
	public void freePosition(Point pos) {
		grid[(int) pos.getX()][(int) pos.getY()] = false;
	}

	/**
	 * Reset the entire grid to empty.
	 */
	public void resetGrid() {
		for(int i = 0; i < gridWidth; i++)
			for(int j = 0; j < gridHeight; j++)
				grid[i][j] = false;        
	}

	///////////////////////////////////////////////////////////////////////////////////Route Planning///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Get the height of the grid
	 * @return The grid height
	 */
	public int getGridHeight() {
		return gridHeight;
	}

	/**
	 * Get the width of the grid
	 * @return The grid width
	 */
	public int getGridWidth() {
		return gridWidth;
	}

	/**
	 * Get the node above the passed-through node
	 * @param node The current node as a point location
	 * @return The point location of the node above
	 */
	public Point getAboveNode(Point node) {
		int x = node.x;
		int y = node.y;

		if (y == 0)
			return null;
		return map[x][y - 1];
	}

	/**
	 * Get the node below the passed-through node
	 * @param node The current node as a point location
	 * @return The point location of the node below
	 */
	public Point getBelowNode(Point node) {
		int x = node.x;
		int y = node.y;

		if (y == gridHeight - 1)
			return null;
		return map[x][y + 1];
	}

	/**
	 * Get the node to the left the passed-through node
	 * @param node The current node as a point location
	 * @return The point location of the node to the left
	 */
	public Point getLeftNode(Point node) {
		int x = node.x;
		int y = node.y;

		if (x == 0)
			return null;
		return map[x - 1][y];
	}

	/**
	 * Get the node to the right the passed-through node
	 * @param node The current node as a point location
	 * @return The point location of the node to the right
	 */
	public Point getRightNode(Point node) {
		int x = node.x;
		int y = node.y;

		if (x == gridWidth - 1)
			return null;
		return map[x + 1][y];
	}

	/**
	 * Get the relative position in the map
	 * @param node1 The first node to use (as a point location)
	 * @param node2 The second node to use (as a point location)
	 * @return A relative position defined by constants, only returns relative position in one direction i.e. never diagonal relative position
	 */
	public Direction getRelativePosition(Point node1, Point node2) {
		if(node2.x > node1.x){
			return Direction.EAST;
		}
		else if(node2.x < node1.x){
			return Direction.WEST;
		}
		else if(node2.y > node1.y){
			return Direction.NORTH;
		}
		else if(node2.y < node1.y){
			return Direction.SOUTH;
		}
		else {//Should never happen
			return null;
		}

	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
