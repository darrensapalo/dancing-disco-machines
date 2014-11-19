package advanos;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;

import advanos.gui.DancingGUIFrame;
import advanos.messages.instances.ReceiveBroadcastUDPMessage;
import advanos.messages.instances.SendNetworkBroadcastMessage;
import advanos.threads.Request;

public class NodeApplication {

	private DancingGUIFrame gui;
	private SendNetworkBroadcastMessage networkBroadcastThread;
	private ReceiveBroadcastUDPMessage receiveUDPThread;

	public static String MULTICAST_GROUP;

	/**
	 * This queue will be used for the updating of Lamport timestamps, which is
	 * used for implementing the mutex within distributed systems.
	 * 
	 * Before a process can enter its critical section, it must first send a CS
	 * request to everyone in the network and it must have received the replies
	 * from all of the systems, totaling to <i>N-1 replies</i>.
	 * 
	 */
	private LinkedList<Request> queue = new LinkedList<Request>();
	private ArrayList<Host> hosts = new ArrayList<Host>();

	private Host leader = null;
	private Host next = null;

	public static String PROCESS_ID;
	public static boolean IS_LEADER;

	public NodeApplication(int port, boolean isLeader) {
		NodeApplication.IS_LEADER = isLeader;
		NodeApplication.PROCESS_ID = ManagementFactory.getRuntimeMXBean().getName();

		gui = new DancingGUIFrame();
		gui.setVisible(true);
		gui.setTitle(PROCESS_ID + " " + gui.getTitle());

		queue = new LinkedList<Request>();

		createNetworkBroadcastThread(port);
		createReceiveBroadcastUDPThread(port);

		if (isLeader) {

		}
	}

	private void createNetworkBroadcastThread(int port) {
		try {
			networkBroadcastThread = new SendNetworkBroadcastMessage(port, null, MULTICAST_GROUP);
			networkBroadcastThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createReceiveBroadcastUDPThread(int port) {
		try {
			receiveUDPThread = new ReceiveBroadcastUDPMessage(this, port, InetAddress.getByName(MULTICAST_GROUP));
			receiveUDPThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public Host getHost(String ipAddress){
		for (Host h : hosts)
			if (h.getIPAddress().equalsIgnoreCase(ipAddress))
				return h;
		
		return null;
	}
	
	public void assignNextInTokenRing(String ipAddress){
		this.next = getHost(ipAddress);
	}
	
	public void addDiscoveredHost(String ipAddress, String[] text) {
		Host h = new Host(ipAddress, text[1].split("@")[0]);
		System.out.println("Detected " + h);
		if (hosts.contains(h) == false) {
			hosts.add(h);
			
			//		     [0]                    [1]                 [2]
			// BROADCAST_ALIVE (IP ADDRESS AND PROCESS ID) <LEADER>
			if (text.length == 3 && text[2].equalsIgnoreCase("LEADER")){
				h.setLeader(true);
				this.leader = h;
			}
		}
		gui.addUser(h);
	}
}