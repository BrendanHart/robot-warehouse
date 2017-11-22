package routePlanning.dataStructures;

import java.awt.Point;

/**
 * Data structure used for route planning to build a linked list of nodes (representing a path)
 * @author Szymon
 *
 */
public class CameFrom {
	public Point fromNode;
	public Point toNode;
	
	public CameFrom(Point fromNode, Point toNode) {
		this.fromNode = fromNode;
		this.toNode = toNode;
	}
}
