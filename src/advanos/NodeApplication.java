package advanos;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import advanos.gui.DancingGUIFrame;
import advanos.messages.instances.ReceiveUDPMessage;
import advanos.messages.instances.SendNetworkBroadcastMessage;
import advanos.messages.receive.TCPServer;
import advanos.messages.receive.TCPSocketHandler;

public class NodeApplication {

	private DancingGUIFrame gui;
	
	private SendNetworkBroadcastMessage networkBroadcastThread;
	private ReceiveUDPMessage receiveUDPThread;


	public static String MULTICAST_GROUP;

	private ArrayList<Host> hosts = new ArrayList<Host>();

	
	private TCPSocketHandler leaderHandler;
	private Host leader = null;
	
	private TCPSocketHandler nextHandler;
	private Host next = null;
	
	private TCPServer listen;
	
	
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
		gui.setTitle(PROCESS_ID + " " + gui.getTitle());

		// Broadcast UDP threads
		networkBroadcastThread = SendNetworkBroadcastMessage.create(port, MULTICAST_GROUP);
		networkBroadcastThread.start();
		
		receiveUDPThread = ReceiveUDPMessage.create(this, port, MULTICAST_GROUP);
		receiveUDPThread.start();
		
		// Listen TCP thread
		listen = TCPServer.create(this, port);
		listen.start();
		
	}
	
	public Host getHost(String ipAddress){
		for (Host h : hosts)
			if (h.getIPAddress().equalsIgnoreCase(ipAddress))
				return h;
		return null;
	}


	private void updateNextTCPconnection(Host lastHost, Host newHost) {
		TCPSocketHandler lastHostSH = listen.getTCPSocketHandler(lastHost);
		lastHostSH.sendMessage("ASSIGN NEXT " + newHost.getIPAddress());
	}
	
	public synchronized void createNextTCPconnection(String ipAddress){
		Host host = getHost(ipAddress);
		createNextTCPconnection(host);
	}
	
	public synchronized void createNextTCPconnection(Host newHost){
		next = newHost;
		if (IS_LEADER == false || next != leader)
			nextHandler = TCPSocketHandler.create(this, newHost, port); 
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
				leader = newHost;
				leaderHandler = TCPSocketHandler.create(this, leader, port);
			}
			if (NodeApplication.IS_LEADER)
				organizeNetworkRingTopology(lastHost, newHost);
			
		}
		gui.addUser(newHost);
	}

	private void organizeNetworkRingTopology(Host lastHost, Host newHost) {
		System.err.println("Beginning to fix network topology...");
		
		
		if (lastHost != null){
			if (lastHost.equals(this.leader)){
				System.out.println("First client host was identified. The leader host is connected to the first client host.");
				createNextTCPconnection(newHost);
			}else{
				System.out.println("The last cleint host " + lastHost + " is connected to the new client host " + newHost + ".");
				updateNextTCPconnection(lastHost, newHost);
			}
		}
		
		
		if (leader != null && newHost.equals(leader) == false){
			System.err.println("Since I already know who the leader is, I'll assign the newest node to send to the leader.");
			updateNextTCPconnection(newHost, leader);
		}else if (hosts.size() == 1){
			System.err.println("I found only one person in the network. That person will be next after me.");
			createNextTCPconnection(newHost);
		}
		
		System.err.println("Finished fixing network topology.");
		System.out.println();
		
		// As a leader, try to dance
		attemptToDance();
	}


	public synchronized void attemptToDance(){
		boolean hasDanced = false;
		do {
			try {
				// Only try to dance once every second. Put a delay of one second.
				Thread.sleep(1000);
				if (next != null){
					if (TOKEN){
						double s = Math.random() * 100;
						if (s > 90){ // 10% chance to dance
							gui.dance();
						}
						if (next != leader || NodeApplication.IS_LEADER == false)
							releaseToken(next);
					}
					hasDanced = true;
				}else{
					if (leader != null)
						leaderHandler.sendMessage("REQUEST NEXT");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}while(hasDanced == false);
	}

	private void releaseToken(Host next) {
		TOKEN = false;
		System.out.println("Releasing token, giving it to " + next);
		nextHandler.sendMessage("SEND TOKEN");
	}

	public void confirmReceiptOfToken(Host host) {
		TOKEN = true;
		System.out.println("Received token from " + host + "! Confirming...");
		TCPSocketHandler fromHost = listen.getTCPSocketHandler(host);
		fromHost.sendMessage("RECEIVED TOKEN");

		// Check if you want to dance
		attemptToDance();
	}

	public void receiveSentTokenConfirmation(String[] text, String ipAddress) {
		NodeApplication.TOKEN = false;
	}

	public void reAssignNextInToken(String ipAddress) {
		Host from = getHost(ipAddress);
		int indexOf = hosts.indexOf(from);
		Host to = (indexOf == hosts.size() - 1) ? leader : hosts.get(indexOf + 1);
		updateNextTCPconnection(from, to);
	}

	public void confirmReceiptOfToken(String ipAddress) {
		confirmReceiptOfToken(getHost(ipAddress));
	}

	
}