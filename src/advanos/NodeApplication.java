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
import advanos.messages.instances.SendRingRequestMessage;
import advanos.messages.instances.SendTokenConfirmedMessage;
import advanos.messages.instances.SendTokenMessage;
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

	private boolean wantToDance;
	
	public static boolean TOKEN = false;

	public NodeApplication(int port, boolean isLeader) {
		this.port = port;
		NodeApplication.TOKEN = NodeApplication.IS_LEADER = isLeader;
		NodeApplication.PROCESS_ID = ManagementFactory.getRuntimeMXBean().getName();

		gui = new DancingGUIFrame();
		gui.setVisible(true);
		gui.setTitle(PROCESS_ID + " " + gui.getTitle());

		queue = new LinkedList<Request>();

		createNetworkBroadcastThread(port);
		createReceiveUDPThread(port);
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
	
	public synchronized void setNextInTokenRing(String ipAddress){
		this.next = getHost(ipAddress);
		System.out.println("I now know who is next after me: " + this.next);
	}
	
	public void assignNextInTokenRing(Host from, Host to){
		try {
			SendRingAssignmentMessage message = new SendRingAssignmentMessage(port, "ASSIGN " + to.getIPAddress(), from.getIPAddress());
			message.start();
			
			// System.out.println(from + " assigned to send to " + to);
		} catch (SocketException | UnsupportedEncodingException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public Host getLastHost(){
		if (hosts.size() == 0) return null;
		return hosts.get(hosts.size() - 1);
	}
	
	public synchronized void addDiscoveredHost(String ipAddress, String[] text) {
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
				System.err.println("Beginning to fix network topology...");
				if (lastHost != null){
					if (lastHost.equals(this.leader)){
						System.err.println("Leader assigned to first node.");
						this.next = newHost;
					}else{
						System.err.println("The last node, " + lastHost + " is attached to the new node, " + newHost);
						assignNextInTokenRing(lastHost, newHost);
					}
				}
				
				
				if (this.leader != null && newHost.equals(this.leader) == false){
					System.err.println("Since I already know who the leader is, I'll assign the newest node to send to the leader.");
					assignNextInTokenRing(newHost, this.leader);
				}else{
					System.err.println("I still don't know the leader, or maybe the new host is the leader. I'll just assign myself to send to myself.");
					this.next = newHost;
				// System.out.println("End of reassignment");
				}
				
				attemptToDance();
			}
		}
		gui.addUser(newHost);
	}

	public void receiveSentToken(String[] text, String ipAddress) {
		// You got the token from this guy
		Host host = getHost(ipAddress);

		if (host == null){
			addDiscoveredHost(ipAddress, text);
			host = getHost(ipAddress);
		}
		
		// Inform the guy you received it
		confirmReceiptOfToken(host);
		
		boolean hasDanced = false;
		do {
			try {
				// Only try to dance once every second. Put a delay of one second.
				Thread.sleep(1000);
				if (next != null){
					attemptToDance();
					hasDanced = true;
				}else{
					if (leader != null)
					{
						SendRingRequestMessage sendRingRequestMessage = new SendRingRequestMessage(port, null, leader.getIPAddress());
						sendRingRequestMessage.start();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}while(hasDanced == false);
		
	}
	
	public synchronized void attemptToDance(){
		if (TOKEN){
			double s = Math.random() * 100;
			if (s > 90){ // 10% chance to dance
				gui.dance();
			}
			if (next != leader || NodeApplication.IS_LEADER == false)
				releaseToken(next);
		}
	}

	private void releaseToken(Host next) {
		TOKEN = false;
		System.out.println("Releasing token, giving it to " + next);
		try {
			SendTokenMessage sendTokenMessage = new SendTokenMessage(port, null, next.getIPAddress());
			sendTokenMessage.start();
		} catch (SocketException | UnsupportedEncodingException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void confirmReceiptOfToken(Host host) {
		TOKEN = true;
		System.out.println("Received token from " + host + "! Confirming...");
		try {
			SendTokenConfirmedMessage sendTokenConfirmedMessage = new SendTokenConfirmedMessage(port, null, host.getIPAddress());
			sendTokenConfirmedMessage.start();
		} catch (SocketException | UnsupportedEncodingException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void receiveSentTokenConfirmation(String[] text, String ipAddress) {
		NodeApplication.TOKEN = false;
	}

	public void reAssignNextInToken(String ipAddress) {
		Host from = getHost(ipAddress);
		int indexOf = hosts.indexOf(from);
		Host to = (indexOf == hosts.size() - 1) ? leader : hosts.get(indexOf + 1);
		assignNextInTokenRing(from, to);
	}
}