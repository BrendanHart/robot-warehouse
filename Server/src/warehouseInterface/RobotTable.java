package warehouseInterface;

import Objects.AllRobots;
import Objects.Direction;
import Objects.Sendable.RobotInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A table for displaying robots currently connected with the warehouse
 * @author Artur
 *
 */
public class RobotTable
{
	private static DefaultTableModel tableModel;
	private static JPanel panel;

	/**
	 * Draw the robot table in a panel 
	 * @return A panel with the robot table drawn
	 */
	public static JPanel draw()
	{
		panel = new JPanel(new BorderLayout());
		tableModel = new DefaultTableModel(new String[] {"Robot", "Status"}, 0);
		JTable table = Display.createTable(tableModel);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					int row = table.rowAtPoint(e.getPoint());
					table.setRowSelectionInterval(row, row);
				}
			}
		});

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem viewInfo = new JMenuItem("Information");
		viewInfo.addActionListener(e -> viewRobotInfo(AllRobots.getRobot((String) table.getValueAt(table.getSelectedRow(), 0))));
		popupMenu.add(viewInfo);
		table.setComponentPopupMenu(popupMenu);

		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Display various information about a particular robot
	 * @param robot The robot selected in the table
	 */
	private static void viewRobotInfo(RobotInfo robot)
	{
		JOptionPane.showMessageDialog(panel, "Name: " + robot.getName() + "\nPosition: (" + robot.getPosition().x + ", " + robot.getPosition().y + ")\nDirection: " + robot.getDirection() + "\n");
	}

	/**
	 * Adds a robot to the table
	 * @param robot A robot to add to the table
	 */
	public static void addRobot(RobotInfo robot)
	{
		tableModel.addRow(new Object[] { robot.getName(), "Ready" });
	}

	/**
	 * Remove a particular robot from the table
	 * @param robot The robot to remove from the table
	 */
	public static void removeRobot(RobotInfo robot)
	{
		for(int i = 0; i < tableModel.getRowCount(); i++)
			if(tableModel.getValueAt(i, 0).equals(robot.getName()))
			{
				tableModel.removeRow(i);
				break;
			}
	}

	/**
	 * Update the current status of a particular robot
	 * @param robot The robot concerned
	 * @param status The new status for the robot
	 */
	public static void updateStatus(String robot, String status)
	{
		for(int i = 0; i < tableModel.getRowCount(); i++)
		{
			if(String.valueOf(tableModel.getValueAt(i, 0)).equals(robot))
			{
				tableModel.setValueAt(status, i, 1);
				break;
			}
		}
	}
}
