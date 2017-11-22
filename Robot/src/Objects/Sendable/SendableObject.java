package Objects.Sendable;

/**
 * SHARED OBJECTS
 * Interface for objects that can be sent over the network, all should have the parameters method
 */

/**
 * Should return a string of the parameters that individual class requires to be instantiated
 */
public interface SendableObject {
	public String parameters();
}
