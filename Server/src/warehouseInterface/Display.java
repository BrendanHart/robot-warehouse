package warehouseInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import routeExecution.RouteExecution;
import Objects.AllRobots;
import Objects.Direction;
import Objects.Job;
import Objects.WarehouseMap;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SingleTask;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import jobInput.JobProcessor;

/**
 * A class for testing all classes of the interface
 * @author Artur
 */
public class Display
{
	public static void main(String[] args)
	{
		//JobProcessor.processItemFiles("res/items.csv", "res/locations.csv");
		//JobProcessor.processJobFiles("res/jobs.csv", "res/cancellations.csv");
		
		JFrame frame = new JFrame("Warehouse Interface");
		frame.setLayout(new GridLayout(2, 2));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(800, 600);
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}

		WarehouseMap grid = new WarehouseMap("res/drops.csv");
		
		frame.add(JobTable.draw(new RouteExecution(grid)));
		frame.add(GridMap.createGrid(grid));
		frame.add(RobotTable.draw());
		frame.add(Statistics.draw());
		
		AllRobots.addRobot(new RobotInfo("TayTay", new Point(5, 7)));
		//AllRobots.addRobot(new RobotInfo("Alfonso", new Point(5, 0), Direction.SOUTH));
		//AllRobots.addRobot(new RobotInfo("John Cena", new Point(0, 4), Direction.WEST));
		//AllRobots.addRobot(new RobotInfo("Donaldihno", new Point(11, 4), Direction.EAST));
		frame.setVisible(true);
	}

	public static JTable createTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel) {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}

			@Override
			public boolean getColumnSelectionAllowed()
			{
				return false;
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //so we can't select more than one row
		table.getTableHeader().setResizingAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.setFocusable(false);
		return table;
	}
}
