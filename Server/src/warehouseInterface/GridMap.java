package warehouseInterface;

import Objects.AllRobots;
import Objects.Direction;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SingleTask;
import Objects.WarehouseMap;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Displays the map of the warehouse, including all robots, drop-off points and items currently being collected by the robots.
 * @author Artur
 *
 */
public class GridMap
{
	private static JPanel panel;
	private static final int GRID_WIDTH = 12, GRID_HEIGHT = 8;
	private static final Color ALFONSO = new Color(255, 215, 0), TAYTAY = new Color(76, 102, 164), CENA = new Color(76, 187, 23);

	/**
	 * Creates the panel that renders the warehouse map
	 * @param grid The description object of the warehouse grid
	 * @return A panel with the rendered warehouse map
	 */
	public static JPanel createGrid(WarehouseMap grid)
	{
		panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				int width = getWidth(), height = getHeight(), xScale = width / 13, yScale = height / 9;
				g2d.clearRect(0, 0, width, height);
				g2d.setStroke(new BasicStroke(10));
				g2d.drawRect(0, 0, width, height); // pretty black border :)
				g2d.setStroke(new BasicStroke(1));
				g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
				g2d.drawString("\u2191N", width - 45, 25);
				g2d.rotate(Math.toRadians(180), width / 2 - 4, height / 2);
				for(int i = 0; i < GRID_WIDTH; i++)
					for(int j = 0; j < GRID_HEIGHT; j++)
					{
						int x = (i + 1) * xScale, y = (j + 1) * yScale, x1 = (i + 2) * xScale, y1 = (j + 2) * yScale;
						if(!grid.isObstacle(i, j))
						{
							g2d.setColor(Color.BLACK);
							if(j < GRID_HEIGHT - 1 && !grid.isObstacle(i, j + 1)) g2d.drawLine(x, y, x, y1);
							if(i < GRID_WIDTH - 1 && !grid.isObstacle(i + 1, j)) g2d.drawLine(x, y, x1, y);
							if(grid.getDropoffPoints().contains(new Point(i, j)))
							{
								g2d.setColor(Color.LIGHT_GRAY);
								g2d.fillRoundRect(x - 4, y - 4, 8, 8, 3, 3);
								g2d.setColor(Color.BLACK);
							}
							else
								g2d.fillOval(x - 3, y - 3, 6, 6);
						}
						else
						{
							g2d.setColor(Color.RED);
							g2d.fillOval(x - 3, y - 3, 6, 6);
							if(j < GRID_HEIGHT - 1 && grid.isObstacle(i, j + 1)) g2d.drawLine(x, y, x, y1);
							if(i < GRID_WIDTH - 1 && grid.isObstacle(i + 1, j) && ((!grid.isObstacle(i, j - 1) && grid.isObstacle(i, j + 1)) || (grid.isObstacle(i, j - 1) && !grid.isObstacle(i, j + 1)))) g2d.drawLine(x, y, x1, y); //only draw red horizontal lines on the edges of the obstacles
						}
					}
				g2d.setStroke(new BasicStroke(3));
				for(RobotInfo robot : AllRobots.getAllRobots())
				{
					switch(robot.getName())
					{
						case "TayTay":
							g2d.setColor(TAYTAY);
							break;
						case "John Cena":
							g2d.setColor(CENA);
							break;
						case "Alfonso":
							g2d.setColor(ALFONSO);
							break;
						default:
							g2d.setColor(Color.BLACK);
					}
					if(robot.getDirection() == Direction.NORTH || robot.getDirection() == Direction.SOUTH)
					{
						g2d.drawRect((GRID_WIDTH - robot.getPosition().x) * xScale - 7, (robot.getPosition().y + 1) * yScale - 14, 14, 28);
						if(robot.getDirection() == Direction.SOUTH)
							g2d.drawLine((GRID_WIDTH - robot.getPosition().x) * xScale, (robot.getPosition().y + 1) * yScale - 14, (GRID_WIDTH - robot.getPosition().x) * xScale, (robot.getPosition().y + 1) * yScale - 22);
						else
							g2d.drawLine((GRID_WIDTH - robot.getPosition().x) * xScale, (robot.getPosition().y + 1) * yScale + 14, (GRID_WIDTH - robot.getPosition().x) * xScale, (robot.getPosition().y + 1) * yScale + 22);
					}
					else
					{
						g2d.drawRect((GRID_WIDTH - robot.getPosition().x) * xScale - 14, (robot.getPosition().y + 1) * yScale - 7, 28, 14);
						if(robot.getDirection() == Direction.WEST)
							g2d.drawLine((GRID_WIDTH - robot.getPosition().x) * xScale + 14, (robot.getPosition().y + 1) * yScale, (GRID_WIDTH - robot.getPosition().x) * xScale + 22, (robot.getPosition().y + 1) * yScale);
						else
							g2d.drawLine((GRID_WIDTH - robot.getPosition().x) * xScale - 14, (robot.getPosition().y + 1) * yScale, (GRID_WIDTH - robot.getPosition().x) * xScale - 22, (robot.getPosition().y + 1) * yScale);
					}
					if(robot.isDoingJob)
					{
						for(int i = robot.currTaskIndex; i < robot.currJob.getNumOfTasks(); i++)
							g2d.fillRoundRect((GRID_WIDTH - robot.currJob.getTaskAtIndex(i).get().getLocation().x) * xScale - 4, (robot.currJob.getTaskAtIndex(i).get().getLocation().y + 1) * yScale - 4, 8, 8, 3, 3);
					}
				}
			}
		};
		return panel;
	}

	/**
	 * Refreshes the warehouse map
	 */
	public static void refresh()
	{
		panel.repaint();
	}
}
