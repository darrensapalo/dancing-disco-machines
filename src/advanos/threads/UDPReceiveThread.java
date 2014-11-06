package advanos.threads;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
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
	private boolean isLeader;

	public UDPReceiveThread(NodeApplication nodeApplication, int port, boolean isLeader) throws SocketException {
		this.isLeader = isLeader;
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
				handleMessage(text, packet.getAddress().toString(), text[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleMessage(String[] text, String ipAddress, String port) {
		switch (text[0].toUpperCase()) {
		case "CS_REQUEST":
			lamportMutexRequest(text, ipAddress, port);
			break;
		case "BROADCAST_ALIVE":
			networkDiscovery(text, ipAddress, port);
			
			if (isLeader)
				leaderRingTopologyManagement(text, ipAddress, port);
			break;
		case "CS_REPLY":
			lamportMutexReply(text, ipAddress, port);
			break;
		}
	}

	private void leaderRingTopologyManagement(String[] text, String ipAddress, String port) {
		int number = 1;
		assignNumber(ipAddress, port, number);
	}

	private void lamportMutexReply(String[] text, String inetAddress, String port) {
		
	}

	private void lamportMutexRequest(String[] text, String inetAddress, String port) {
		// update queue in NodeApplication regarding data in text[]
	}

	private void networkDiscovery(String[] text, String inetAddress, String port) {
		nodeApplication.addDiscoveredHost(inetAddress, text);
	}
	
	private void assignNumber(String ipAddress, String port, int sequence){
		try {
			// Network broadcast thread: broadcasts process ID
			String message = "ASSIGN_NUMBER " + sequence;
			byte[] data = message.getBytes();
			
			Socket s = new Socket(ipAddress, Integer.parseInt(port));
			
			OutputStream outputStream = s.getOutputStream();
			BufferedOutputStream bOS = new BufferedOutputStream(outputStream);
			bOS.write(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
