package advanos.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import advanos.NodeApplication;

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
	private NodeApplication nodeApplication;

	public UDPReceiveThread(NodeApplication nodeApplication, int port) throws SocketException {
		socket = new DatagramSocket(port);
		this.nodeApplication = nodeApplication;
		this.port = port;
		
		System.out.println(nodeApplication.processID + " has successfully created UDP thread bound at port " + port);
	}

	public void run() {
		DatagramPacket packet = new DatagramPacket(new byte[MAX_BYTES],
				MAX_BYTES);

		while (true) {
			try {
				socket.receive(packet);

				String message = new String(packet.getData(), "UTF-8");

				// System.out.println(packet.getAddress () + ":" +
				// packet.getPort ()+":" + message);
				
				String[] text = message.split(" ");
				handleMessage(text, packet.getAddress().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleMessage(String[] text, String string) {
		switch (text[0].toUpperCase()) {
		case "CS_REQUEST":
			lamportMutexRequest(text, string);
			break;
		case "BROADCAST_ALIVE":
			networkDiscovery(text, string);
			break;
		case "CS_REPLY":
			lamportMutexReply(text, string);
			break;
		}
	}

	private void lamportMutexReply(String[] text, String inetAddress) {
		
	}

	private void lamportMutexRequest(String[] text, String inetAddress) {
		// update queue in NodeApplication regarding data in text[]
	}

	private void networkDiscovery(String[] text, String inetAddress) {
		nodeApplication.addDiscoveredHost(inetAddress, text[1]);
	}
}
