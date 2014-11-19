package advanos;

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import advanos.gui.DancingGUIFrame;
import advanos.messages.instances.ReceiveUDPMessage;
import advanos.messages.instances.SendNetworkBroadcastMessage;
import advanos.messages.instances.SendRingAssignmentMessage;
import advanos.threads.Request;

public class NodeApplication {

	private DancingGUIFrame gui;
	
	private SendNetworkBroadcastMessage networkBroadcastThread;
	private ReceiveUDPMessage receiveUDPThread;


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
	private int port;

	public NodeApplication(int port, boolean isLeader) {
		this.port = port;
		NodeApplication.IS_LEADER = isLeader;
		NodeApplication.PROCESS_ID = ManagementFactory.getRuntimeMXBean().getName();

		gui = new DancingGUIFrame();
		gui.setVisible(true);
		gui.setTitle(PROCESS_ID + " " + gui.getTitle());

		queue = new LinkedList<Request>();

		createNetworkBroadcastThread(port);
		createReceiveUDPThread(port);
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

	private void createReceiveUDPThread(int port) {
		try {
			receiveUDPThread = new ReceiveUDPMessage(this, port, InetAddress.getByName(MULTICAST_GROUP));
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
	
	public void setNextInTokenRing(String ipAddress){
		this.next = getHost(ipAddress);
	}
	
	public void assignNextInTokenRing(Host from, Host to){
		try {
			SendRingAssignmentMessage message = new SendRingAssignmentMessage(port, "ASSIGN " + to.getIPAddress(), from.getIPAddress());
			message.start();
			
			System.out.println(from + " assigned to send to " + to);
		} catch (SocketException | UnsupportedEncodingException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public Host getLastHost(){
		if (hosts.size() == 0) return null;
		return hosts.get(hosts.size() - 1);
	}
	
	public void addDiscoveredHost(String ipAddress, String[] text) {
		Host newHost = new Host(ipAddress, text[1].split("@")[0]);
		if (hosts.contains(newHost) == false) {
			Host lastHost = getLastHost();
			hosts.add(newHost);
			
			//		     [0]                    [1]                 [2]
			// BROADCAST_ALIVE (IP ADDRESS AND PROCESS ID) <LEADER>
			if (text.length == 3 && text[2].trim().equalsIgnoreCase("LEADER")){
				newHost.setLeader(true);
				this.leader = newHost;
			}
			
			if (NodeApplication.IS_LEADER){
				if (lastHost != null)
					assignNextInTokenRing(lastHost, newHost);
				
				assignNextInTokenRing(newHost, this.leader);
				System.out.println("End of reassignment");
			}
		}
		gui.addUser(newHost);
	}
}