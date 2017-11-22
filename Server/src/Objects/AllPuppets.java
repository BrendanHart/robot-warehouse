package Objects;

import java.util.ArrayList;

import Objects.Sendable.SendableObject;
import networking.Puppet;

/**
 * SHARED OBJECTS
 * Used to represent puppets (Robot Threads)
 */

public class AllPuppets {

	private static ArrayList<Puppet> puppets = new ArrayList<Puppet>();

	/**
	 * Send an object to a puppet
	 * @param name Who to send the object to
	 * @param obj The object to send
	 */
	public static synchronized void send(String name, SendableObject obj) {
		for(Puppet pup : puppets) {
			if(pup.name().equals(name)) {
				pup.send(obj);
				return;
			}
		}
		// Print out if can't find the puppet
		System.out.println("Could not find puppet " + name + " and send it: " + obj.parameters());
	}

	/**
	 * Get the list of puppets
	 * @return The list of puppets
	 */
	public static synchronized ArrayList<Puppet> getPuppets() {
		return puppets;
	}

	/**
	 * Get the puppet represented by the passed name or return null if there isn't one with that name
	 * @param name The name of the puppet to get
	 * @return The puppet for that name
	 */
	public static synchronized Puppet getPuppet(String name) {
		for(Puppet pup : puppets) {
			if(pup.name().equals(name)) {
				return pup;
			}
		}
		return null;
	}

	/**
	 * Add a puppet
	 * @param pup The puppet to add
	 */
	public static synchronized void addPuppet(Puppet pup) {
		puppets.add(pup);
	}

	/**
	 * Check that a specified puppet exists
	 * @param name The name of the puppet
	 * @return A boolean of whether the specified puppet exists
	 */
	public static synchronized boolean checkExist(String name) {
		for(Puppet pup : puppets) {
			if(pup.name().equals(name)) {
				return true;
			}
		}
		return false;
	}

}
