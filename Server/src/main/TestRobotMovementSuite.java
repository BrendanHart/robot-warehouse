package main;

import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;

import Objects.AllPuppets;
import Objects.Sendable.Move;

public class TestRobotMovementSuite extends Thread {

	public TestRobotMovementSuite() {
		JFrame frame = new JFrame();
		frame.setTitle("Robot Testing Suite");
		frame.setSize(600,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(2,6));
		
		// Alfonso's movement buttons.
		JButton alfFWD = new JButton("Alf FWD");
		alfFWD.addActionListener(e -> AllPuppets.send("Alfonso", new Move('f', new Point(1,1))));
		JButton alfBCK = new JButton("Alf BCK");
		alfBCK.addActionListener(e -> AllPuppets.send("Alfonso", new Move('b', new Point(1,1))));
		JButton alfLFT = new JButton("Alf LFT");
		alfLFT.addActionListener(e -> AllPuppets.send("Alfonso", new Move('l', new Point(1,1))));
		JButton alfRGT = new JButton("Alf RGT");
		alfRGT.addActionListener(e -> AllPuppets.send("Alfonso", new Move('r', new Point(1,1))));
		
		// Tay Tays's movement buttons.
		JButton tayFWD = new JButton("Tay FWD");
		tayFWD.addActionListener(e -> AllPuppets.send("Tay Tay", new Move('f', new Point(1,1))));
		JButton tayBCK = new JButton("Tay BCK");
		tayBCK.addActionListener(e -> AllPuppets.send("Tay Tay", new Move('b', new Point(1,1))));
		JButton tayLFT = new JButton("Tay LFT");
		tayLFT.addActionListener(e -> AllPuppets.send("Tay Tay", new Move('l', new Point(1,1))));
		JButton tayRGT = new JButton("Tay RGT");
		tayRGT.addActionListener(e -> AllPuppets.send("Tay Tay", new Move('r', new Point(1,1))));
		
		// John Cena's movement buttons.
		JButton jonFWD = new JButton("Jon FWD");
		jonFWD.addActionListener(e -> AllPuppets.send("John Cena", new Move('f', new Point(1,1))));
		JButton jonBCK = new JButton("Jon BCK");
		jonBCK.addActionListener(e -> AllPuppets.send("John Cena", new Move('b', new Point(1,1))));
		JButton jonLFT = new JButton("Jon LFT");
		jonLFT.addActionListener(e -> AllPuppets.send("John Cena", new Move('l', new Point(1,1))));
		JButton jonRGT = new JButton("Jon RGT");
		jonRGT.addActionListener(e -> AllPuppets.send("John Cena", new Move('r', new Point(1,1))));
		
		// Group movement buttons.
		JButton allFWD = new JButton("All FWD");
		allFWD.addActionListener(e -> {
			AllPuppets.send("Alfonso", new Move('f', new Point(1,1)));
			AllPuppets.send("Tay Tay", new Move('f', new Point(1,1)));
			AllPuppets.send("John Cena", new Move('f', new Point(1,1)));
		});
		JButton allBCK = new JButton("All BCK");
		allBCK.addActionListener(e -> {
			AllPuppets.send("Alfonso", new Move('b', new Point(1,1)));
			AllPuppets.send("Tay Tay", new Move('b', new Point(1,1)));
			AllPuppets.send("John Cena", new Move('b', new Point(1,1)));
		});
		JButton allLFT = new JButton("All LFT");
		allLFT.addActionListener(e -> {
			AllPuppets.send("Alfonso", new Move('l', new Point(1,1)));
			AllPuppets.send("Tay Tay", new Move('l', new Point(1,1)));
			AllPuppets.send("John Cena", new Move('l', new Point(1,1)));
		});
		JButton allRGT = new JButton("All RGT");
		allRGT.addActionListener(e -> {
			AllPuppets.send("Alfonso", new Move('r', new Point(1,1)));
			AllPuppets.send("Tay Tay", new Move('r', new Point(1,1)));
			AllPuppets.send("John Cena", new Move('r', new Point(1,1)));
		});
		
		// Adds all buttons to frame.
		frame.add(alfFWD);
		frame.add(alfBCK);

		frame.add(tayFWD);
		frame.add(tayBCK);

		frame.add(jonFWD);
		frame.add(jonBCK);

		frame.add(allFWD);
		frame.add(allBCK);

		frame.add(alfLFT);
		frame.add(alfRGT);

		frame.add(tayLFT);
		frame.add(tayRGT);

		frame.add(jonLFT);
		frame.add(jonRGT);

		frame.add(allLFT);
		frame.add(allRGT);
		
		frame.setVisible(true);
	}
}
