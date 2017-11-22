package warehouseInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import routeExecution.RouteExecution;
import Objects.AllRobots;
import Objects.Job;
import Objects.Sendable.SingleTask;
import jobInput.JobProcessor;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * A table for displaying the jobs currently being carried out by the robots
 * @author Artur
 *
 */
public class JobTable
{
	private static JTable activeJobs;
	private static DefaultTableModel tableModel;
	private static JPanel panel;
	private static RouteExecution routeExec;

	/**
	 * Draws the job table in a panel
	 * @param routeExec Route Execution object
	 * @return A panel with the job table drawn
	 */
	public static JPanel draw(RouteExecution routeExec)
	{
		panel = new JPanel(new BorderLayout());
		tableModel = new DefaultTableModel(new String[] {"Job ID", "Reward", "Robot", "Status"}, 0);
		activeJobs = Display.createTable(tableModel);
		activeJobs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					int row = activeJobs.rowAtPoint(e.getPoint());
					activeJobs.setRowSelectionInterval(row, row);
				}
			}
		});
		JobTable.routeExec = routeExec;

		JButton cancelJobManually = new JButton("Cancel a job");
		cancelJobManually.addActionListener(e -> cancelJobManually());

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem viewInfo = new JMenuItem("Information");
		viewInfo.addActionListener(e -> viewJobInfo((int) activeJobs.getValueAt(activeJobs.getSelectedRow(), 0)));
		JMenuItem cancelJob = new JMenuItem("Cancel this job");
		cancelJob.addActionListener(e -> cancelJob((int) activeJobs.getValueAt(activeJobs.getSelectedRow(), 0), (String) activeJobs.getValueAt(activeJobs.getSelectedRow(), 2)));
		popupMenu.add(viewInfo);
		popupMenu.add(cancelJob);
		activeJobs.setComponentPopupMenu(popupMenu);
		activeJobs.getColumnModel().getColumn(0).setMaxWidth(60);
		activeJobs.getColumnModel().getColumn(0).setPreferredWidth(60);
		activeJobs.getColumnModel().getColumn(1).setMaxWidth(60);
		activeJobs.getColumnModel().getColumn(1).setPreferredWidth(60);
		activeJobs.getColumnModel().getColumn(2).setMaxWidth(100);
		activeJobs.getColumnModel().getColumn(2).setPreferredWidth(100);

		panel.add(new JScrollPane(activeJobs), BorderLayout.CENTER);
		panel.add(cancelJobManually, BorderLayout.SOUTH);
		return panel;
	}

	private static void cancelJobManually()
	{
		String jobID = (String) JOptionPane.showInputDialog("Type in the ID of the job which you wish to cancel:");
		if(jobID != null)
		{
			if(jobID.matches("\\d+"))
			{
				if(JobProcessor.getJob(Integer.parseInt(jobID)) != null)
				{
					int parse = Integer.parseInt(jobID);
					JobProcessor.getJob(parse).cancel();
					Statistics.jobCancelled();
					JOptionPane.showMessageDialog(panel, "Job (ID " + parse + ") cancelled.");
				}
				else
				{
					JOptionPane.showMessageDialog(panel, "Job with given ID doesn't exist!");
					cancelJobManually();
				}
			}
			else
			{
				JOptionPane.showMessageDialog(panel, "ID must be an integer!");
				cancelJobManually();
			}
		}
	}

	/**
	 * Add a job to the job table
	 * @param job Job ID
	 * @param reward The total reward for a job
	 * @param robot The robot carrying out the job
	 */
	public static void addJob(int job, String reward, String robot)
	{
		tableModel.addRow(new Object[] { job, reward, robot, "Running task"});
	}

	/**
	 * Cancels a job being carried out by the robot
	 * @param jobID Job ID
	 * @param robot Robot executing the job
	 * @param routeExec Route Execution object
	 */
	private static void cancelJob(int jobID, String robot)
	{
		updateStatus(jobID, "Cancelled");
		RobotTable.updateStatus(robot, "Ready");
		JobProcessor.getJob(jobID).cancel();
		//routeExec.initVariables(robot);
		String name=robot;
		AllRobots.getRobot(name).isDoingJob=false;
		AllRobots.getRobot(name).pickingUp=false;
		AllRobots.getRobot(name).droppingOff=false;
		AllRobots.getRobot(name).hasATask=false;
		AllRobots.getRobot(name).finishedDroppingItems=false;
		AllRobots.getRobot(name).waitingForMoveReport=false;
		AllRobots.getRobot(name).hasMoved=false;
		AllRobots.getRobot(name).hasCompletedTask=false;
		AllRobots.getRobot(name).currDirectionsIndex=0;
		AllRobots.getRobot(name).currTaskIndex=0;
		AllRobots.getRobot(name).goingToDropOff=false;
		Statistics.jobCancelled();
		System.out.println("job cancelled");
		JOptionPane.showMessageDialog(panel, "Job " + jobID + " cancelled.");
	}

	/**
	 * Display information about a particular job in a message box
	 * @param jobID Job ID
	 */
	private static void viewJobInfo(int jobID)
	{
		Job job = JobProcessor.getJob(jobID);
		String tasks = "";
		for(SingleTask task : job.getTasks())
			if(!task.getItemID().equals("dropOff"))
				tasks += "(" + task.getItemID() + ", " + task.getQuantity() + ") ";
		JOptionPane.showMessageDialog(panel, "Job ID: " + jobID + "\nTasks: " + tasks + "\nCancellation probability: " + job.getCancellationProb() + "\nTotal weight: " + job.getTotalWeight());
	}
	
	/**
	 * Update the status of a particular job
	 * @param jobID Job ID
	 * @param status New status of the job
	 */
	public static void updateStatus(int jobID, String status)
	{
		for(int i = 0; i < tableModel.getRowCount(); i++)
		{
			if((int)(tableModel.getValueAt(i, 0)) == jobID)
			{
				tableModel.setValueAt(status, i, 3);
				break;
			}
		}
	}
}
