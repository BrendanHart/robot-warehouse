package warehouseInterface;

import Objects.AllRobots;
import Objects.Sendable.RobotInfo;
import Objects.Sendable.SingleTask;
import jobInput.JobProcessor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * DEPRECIATED: Class that allows the operator to select a job to assign to the robot
 * @author Artur
 *
 */
public class AddJob extends JFrame
{
	private JTable table;

	/**
	 * Displays the window with a list of jobs to assign to the robots
	 */
	public AddJob()
	{
		super("Add a new job");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(640, 480);

		DefaultTableModel tableModel = new DefaultTableModel(new String[] { "Job ID", "Tasks", "Reward" }, 0);
		table = new JTable(tableModel) {
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
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //so we can't select more than one job
		table.getTableHeader().setResizingAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(0).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		table.setAutoCreateRowSorter(true);

		for(int i = 10000; i < 10100; i++)
		{
			String tasks = "";
			double reward = 0;
			for(SingleTask task : JobProcessor.getJob(i).getTasks())
			{
				tasks += "(" + task.getItemID() + ", " + task.getQuantity() + ") ";
				reward += JobProcessor.getItem(task.getItemID()).getReward();
			}
			tableModel.addRow(new Object[] { i, tasks, String.format("%.2f", reward) });
		}

		JButton ok = new JButton("OK");
		ok.addActionListener(e -> addJob());

		JPanel comp = new JPanel(new BorderLayout());
		comp.add(new JScrollPane(table), BorderLayout.CENTER);
		comp.add(ok, BorderLayout.SOUTH);

		add(comp);
		setVisible(true);
	}

	/**
	 * Adds a selected job to the job table
	 */
	private void addJob()
	{
		if(table.getSelectedRow() != -1) //we know a row has been selected
		{
			ArrayList<String> robots = AllRobots.getAllRobots().stream().map(RobotInfo::getName).collect(Collectors.toCollection(ArrayList::new));
			String robot = (String) JOptionPane.showInputDialog(this, "Select the robot which should carry out this job:", "Add a new job", JOptionPane.PLAIN_MESSAGE, null, robots.toArray(), 0);
			if(robot != null) //we know that the user hasn't cancelled or closed the window
			{
				JobTable.addJob((int) table.getValueAt(table.getSelectedRow(), 0), (String) table.getValueAt(table.getSelectedRow(), 2), robot);
				dispose();
			}
		}
		else
			JOptionPane.showMessageDialog(this, "Select a job first!", "Error", JOptionPane.PLAIN_MESSAGE);
	}
}
