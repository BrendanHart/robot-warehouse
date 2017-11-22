package networking;

import java.io.DataOutputStream;
import java.io.IOException;

import Objects.Sendable.SendableObject;


/**
 * Sends to the server
 *  
 */
public class ClientSender {

	private static DataOutputStream toServer;

	public static void setStream(DataOutputStream stream){
		toServer = stream;
	}

	/**
	 * Only done so that access to the commands ArrayList is always synchronised.
	 * @param comm The command to add.
	 * @throws IOException 
	 */
	synchronized static public void send(SendableObject comm){
		String dissolve = comm.parameters();
		try {
			toServer.writeUTF(dissolve);
			toServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
