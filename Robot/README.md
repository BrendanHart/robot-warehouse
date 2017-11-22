<h1> Group 1.1 - Robot </h1> 

This repo contains all the code that is to be run on the NXT Robots.

All the main code is contained within the master branch.

All of the testing code is contained under the test_scripts branch.

Previous RobotMotion is contained within the named branch. All other branches are kept purely for evidence of code revision.

<h2> Setup </h2>

When setup in Eclipse, it is a NXT project. It requires requires rp-shared and rp-utils on its build path. 

It needs the LeJOS NXT runtime.

<h2> Robots </h2>

This code is designed for connection to our 3 group robots:

<ul>
    <li> TayTay </li>
    <li> John Cena </li>
    <li> Alfonso </li>
</ul>

<h2> Running </h2>

The whole code base is called from <b>RunRobot.java</b> in the <i> main </i> package.

RunRobotLoc.java is used for when localisation is to be used to determine the initial position of the robot.

These files should be run (as a NXT project) in Eclipse, so that they copy onto the robot, they can then be run directly on the robot, and open starting will wait for a bluetooth connection with the server.

