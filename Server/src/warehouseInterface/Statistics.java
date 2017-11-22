package warehouseInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.Random;

/**
 * Display and keep track of revenue and jobs done/cancelled in a session.
 * @author Artur
 */
public class Statistics
{
	private static DefaultTableModel tableModel;
	private static double revenue;
	private static int jobsDone, jobsCancelled;

	/**
	 * Draw the table with various statistics
	 * @return A panel with the statistics table
	 */
	public static JPanel draw()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JLabel title = new JLabel("Warehouse Interface");
		title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		tableModel = new DefaultTableModel(new String[] { "Revenue made", "Jobs done", "Jobs cancelled" }, 1);
		JTable table = new JTable(tableModel) {
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		table.getTableHeader().setResizingAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.setFocusable(false);
		table.setRowSelectionAllowed(false);
		for(int i = 0; i < 3; i++)
			table.setValueAt(0, 0, i); //set default values for stats

		panel.add(title);
		panel.add(new JScrollPane(table));
		panel.add(Box.createRigidArea(new Dimension(0, 30)));
		panel.add(new JLabel(new ImageIcon("res/1.1 Logo.png")));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
		return panel;
	}

	/**
	 * Increase the revenue counter
	 * @param amount The amount to increase the revenue by
	 */
	public static void increaseRevenue(double amount)
	{
		revenue += amount;
		tableModel.setValueAt(String.format("%.2f", revenue), 0, 0);
	}

	/**
	 * Indicate that a job has been completed
	 */
	public static void jobDone()
	{
		jobsDone++;
		tableModel.setValueAt(jobsDone, 0, 1);
	}

	/**
	 * Indicate that a job has been cancelled
	 */
	public static void jobCancelled()
	{
		jobsCancelled++;
		tableModel.setValueAt(jobsCancelled, 0, 2);
	}
}
