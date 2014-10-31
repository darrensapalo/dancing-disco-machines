package advanos.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * <p>This thread handles receiving UDP information.
 * The data passed around is a string converted into bytes.
 * The following section provides example content of the UDP payload.</p>
 * 
 * <p>Please note that this is tentative and is pending inspection. This is basically the so called
 * <b>protocol</b> that sir Sol was asking us to agree upon so that our systems can communicate.
 * </p>
 * <p>
 * <h3>CS_REQUEST PID TIMESTAMP</h3>
 * e.g. "CS_REQUEST 1001 1"
 * </p>
 * 
 * <p>
 * <h3>CS_REPLY PID</h3>
 * e.g. "CS_REPLY 1001"
 * </p>
 * 
 * <p>
 * <h3>BROADCAST_ALIVE PID</h3>
 * e.g. "BROADCAST_ALIVE Hi I'm alive."
 * </p> 
 * @author Darren
 *
 */
public class UDPReceiveThread extends Thread {

	private int port = 1234;
	private DatagramSocket socket;
	public static final int MAX_BYTES = 100;

	public UDPReceiveThread(int port) {
		this.port = port;
	}

	public void run() {
		try {
			socket = new DatagramSocket(port);
		} catch (Exception ex) {
			System.out.println("Problem creating socket on port: " + port);
		}

		DatagramPacket packet = new DatagramPacket(new byte[MAX_BYTES],
				MAX_BYTES);

		while (true) {
			try {
				socket.receive(packet);

				String message = new String(packet.getData(), "UTF-8");

				// System.out.println(packet.getAddress () + ":" +
				// packet.getPort ()+":" + message);

				String[] text = message.split(" ");
				handleMessage(text);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleMessage(String[] text) {
		switch (text[0].toUpperCase()) {
		case "CS_REQUEST":
			lamportMutexRequest(text);
			break;
		case "BROADCAST_ALIVE":
			networkDiscovery(text);
			break;
		case "CS_REPLY":
			lamportMutexReply(text);
			break;
		}
	}

	private void lamportMutexReply(String[] text) {
		
	}

	private void lamportMutexRequest(String[] text) {
		// update queue in NodeApplication regarding data in text[]
	}

	private void networkDiscovery(String[] text) {

	}
}
