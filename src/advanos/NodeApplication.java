package advanos;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
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
	private LinkedList<Request> queue = new LinkedList<Request>();
	private ArrayList<Host> hosts = new ArrayList<Host>();
	private int currentPort;
	
	public String processID;

	public NodeApplication(int port) {
		currentPort = port;
		processID = ManagementFactory.getRuntimeMXBean().getName();
		
		gui = new DancingGUIFrame();
		gui.setVisible(true);
		gui.setTitle(processID + " " + gui.getTitle());
		
		queue = new LinkedList<Request>();
		
		createTCPThread(port);
		
		createUDPThread(port);
		createNetworkBroadcastThread(port);
		
	}

	private void createNetworkBroadcastThread(int port) {
		do {
			try {
				// Network broadcast thread: broadcasts process ID
				String message = "BROADCAST_ALIVE " + processID;
				byte[] data = message.getBytes();
				
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
				udpReceiveThread = new UDPReceiveThread(this, port);
				udpReceiveThread.start();
			} catch (Exception e) {
				System.err.println("Port " + port + " already in use in another UDP thread.");
				port++;
			}
		} while (udpReceiveThread == null);
	}

	private void createTCPThread(int port) {
		// Get a server thread up and running
		do {
			try {
				// TCP receive thread
				tcpReceiveThread = new TCPReceiveThread(this, port);
				tcpReceiveThread.start();

			} catch (IOException e) {
				System.err.println("Port " + port + " already in use in another TCP thread.");
				port++;
			}
		} while (tcpReceiveThread == null);
	}

	public void addDiscoveredHost(String ipAddress, String processID) {
		Host h = new Host(ipAddress, processID);
		if (hosts.contains(h) == false){
			hosts.add(h);
		}
		gui.addUser(h);
	}
	
	
}
