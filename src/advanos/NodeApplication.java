package advanos;

import java.io.IOException;
import java.util.LinkedList;

import advanos.gui.DancingGUIFrame;
import advanos.threads.NetworkBroadcastThread;
import advanos.threads.Request;
import advanos.threads.TCPReceiveThread;
import advanos.threads.UDPReceiveThread;

public class NodeApplication {
	private TCPReceiveThread tcpReceiveThread;
	private DancingGUIFrame gui;
	private NetworkBroadcastThread networkBroadcastThread;
	private UDPReceiveThread udpReceiveThread;
	
	/**
	 * This queue will be used for the updating of Lamport timestamps,
	 * which is used for implementing the mutex within distributed systems.
	 * 
	 * Before a process can enter its critical section, it must first send
	 * a CS request to everyone in the network and it must have received 
	 * the replies from all of the systems, totaling to <i>N-1 replies</i>.  
	 * 
	 */
	private LinkedList<Request> queue;

	public NodeApplication(int port) {
		gui = new DancingGUIFrame();
		gui.setVisible(true);
		
		queue = new LinkedList<Request>();

		createTCPThread(port);
		createUDPThread(port);
		createNetworkBroadcastThread(port);
		
	}

	private void createNetworkBroadcastThread(int port) {
		do {
			try {
				// Network broadcast thread
				byte[] data = "I'm alive.".getBytes();
				networkBroadcastThread = new NetworkBroadcastThread(port, data);
				networkBroadcastThread.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (networkBroadcastThread == null);
	}

	private void createUDPThread(int port) {
		do {
			try {

				// UDP receive thread
				udpReceiveThread = new UDPReceiveThread(port);
				udpReceiveThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (udpReceiveThread == null);
	}

	private void createTCPThread(int port) {
		// Get a server thread up and running
		do {
			try {
				// TCP receive thread
				tcpReceiveThread = new TCPReceiveThread(port);
				tcpReceiveThread.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (tcpReceiveThread == null);
	}
	
	
}
