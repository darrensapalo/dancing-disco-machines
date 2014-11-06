package advanos;

import java.io.IOException;
import java.io.Serializable;
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
	
	private Host leader = null;
	private Host next = null;
	private int currentPort;
	
	public String processID;
	private boolean isLeader;

	public NodeApplication(int port, boolean isLeader) {
		currentPort = port;
		this.isLeader = isLeader;
		processID = ManagementFactory.getRuntimeMXBean().getName();
		
		gui = new DancingGUIFrame();
		gui.setVisible(true);
		gui.setTitle(processID + " " + gui.getTitle());
		
		queue = new LinkedList<Request>();
		
		createTCPThread(port);
		
		createUDPThread(port, isLeader);
		createNetworkBroadcastThread(port, isLeader);
		
		if (isLeader){
			
		}
		
		// wait -- random time 2s - 5s
		// request for cs
	}
	
	
	private void createNetworkBroadcastThread(int port, boolean isLeader) {
		do {
			try {
				// Network broadcast thread: broadcasts process ID
				String message = "BROADCAST_ALIVE " + processID + " " + port;
				if (isLeader)
					message += " LEADER";
				byte[] data = message.getBytes();
				
				networkBroadcastThread = new NetworkBroadcastThread(port, data);
				networkBroadcastThread.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (networkBroadcastThread == null);
	}

	private void createUDPThread(int port, boolean isLeader) {
		do {
			try {

				// UDP receive thread
				udpReceiveThread = new UDPReceiveThread(this, port, isLeader);
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

	public void addDiscoveredHost(String ipAddress, String[] text) {
		Host h = new Host(ipAddress, text[1]);
		if (text.length == 4 && text[3].equals("LEADER"))
			h.setLeader(true);
		
		if (hosts.contains(h) == false){
			hosts.add(h);
		}
		gui.addUser(h);
	}
}

interface Message extends Serializable {};

class CommunicationMessage implements Message {
	int sendingProcessNumber;
	int time;
	int receivingProcessNumber;
	
	public CommunicationMessage(int sendingProcessNumber, int time,
			int receivingProcessNumber) {
		this.sendingProcessNumber = sendingProcessNumber;
		this.time = time;
		this.receivingProcessNumber = receivingProcessNumber;
	}
}