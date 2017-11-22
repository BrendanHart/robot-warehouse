package Objects;

import java.util.ArrayList;

/**
 * SHARED OBJECTS
 * Used to hold commands
 */
public class CommandHolder {

	private static ArrayList<Commands> commands = new ArrayList<Commands>();

	/**
	 * Pop a command (return the top command and return it, removing it from the list)
	 * @return The popped command
	 */
	public static synchronized Commands pop() {
		Commands nextComm = commands.get(0);
		commands.remove(0);
		return nextComm;
	}

	/**
	 * Add a command
	 * @param person The person responsible
	 * @param command The command to add
	 */
	public static synchronized void add(String person, Object command) {
		Commands newComm = new Commands(person, command);
		commands.add(newComm);
	}
}
