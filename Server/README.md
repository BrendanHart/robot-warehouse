<h1> Group 1.1 - Server </h1> 

This repo contains all the code that is to be run on the server.

The best device for connection is one of the lab machines, equipped with a bluetooth dongle.

All the main code is contained within the master branch.

All of the testing code is contained under the test_scripts branch.

All other branches are kept purely for evidence of code revision.

<h2> Setup </h2>

When setup in Eclipse, it is a standard Java project. It requires requires rp-shared and rp-utils on its build path. 

It needs the JRE 8 System Libary, JUnit 4 and the LeJOS PC libraries.

<h2> Robots </h2>

This code is designed for connection to our 3 group robots. This code won't run on different robots without changes to the configuration.

<ul>
    <li> TayTay </li>
    <li> John Cena </li>
    <li> Alfonso </li>
</ul>

<h2> Pairing Robots </h2>

In order to work, each robot has to be paired to the computer running the server code before it can connect via bluetooth.

<ol>
    <li> Plug in the bluetooth dongle </li>
    <li> Go into settings </li>
    <li> Switch on the robot, and ensure bluetooth visibility is on </li>
    <li> On the computer, search for devices until you find the robot's name </li>
    <li> Click the robot's name, then select 'pin options' and choose '1234' (or whatever is set on the robot in the bluetooth menu) </li>
    <li> Click okay and follow the instructions </li>
    <li> The robot should be paired successfully </li>
    <li> Rinse and repeat </li>
</ol>

<h2> Running </h2>

The whole code base is called from <b>RunServer.java</b> in the <i> main </i> package.

The code does not need to be modified to use 3 robots in multi-root planning. To use less robots, comment out Puppet x = new ... and AllPuppets.addPuppet(x) in RunServer.java


