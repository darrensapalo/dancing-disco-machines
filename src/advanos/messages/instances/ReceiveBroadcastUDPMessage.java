package advanos.messages.instances;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import advanos.NodeApplication;
import advanos.messages.receive.ReceiveMulticastMessage;

public class ReceiveBroadcastUDPMessage extends ReceiveMulticastMessage
{
	
	public ReceiveBroadcastUDPMessage(NodeApplication nodeApplication, int port, InetAddress group) throws IOException {
		super(nodeApplication, port, group);
	}

	@Override
	protected void handleMessage(String[] text, String ipAddress) {
		switch (text[0].toUpperCase()) {
		case "CS_REQUEST":
			lamportMutexRequest(text, ipAddress);
			break;
		case "BROADCAST_ALIVE":
			networkDiscovery(text, ipAddress);
			
			if (NodeApplication.IS_LEADER) leaderRingTopologyManagement(text, ipAddress);
			break;
		case "CS_REPLY":
			lamportMutexReply(text, ipAddress);
			break;
		}
	}

	private void leaderRingTopologyManagement(String[] text, String ipAddress) {
//		int number = 1;
//		assignNumber(ipAddress, number);
	}

	private void lamportMutexReply(String[] text, String ipAddress) {
		
	}

	private void lamportMutexRequest(String[] text, String ipAddress) {
		// update queue in NodeApplication regarding data in text[]
	}

	private void networkDiscovery(String[] text, String ipAddress) {
		nodeApplication.addDiscoveredHost(ipAddress, text);
	}
	
	private void assignNumber(String ipAddress, int sequence){
		try {
			// Network broadcast thread: broadcasts process ID
			String message = "ASSIGN_NUMBER " + sequence;
			byte[] data = message.getBytes();
			
			Socket s = new Socket(ipAddress, port);
			
			OutputStream outputStream = s.getOutputStream();
			BufferedOutputStream bOS = new BufferedOutputStream(outputStream);
			bOS.write(data);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

