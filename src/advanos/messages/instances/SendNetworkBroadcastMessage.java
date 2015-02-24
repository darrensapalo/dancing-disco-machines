package advanos.messages.instances;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import advanos.NodeApplication;
import advanos.messages.send.SendRepeatingMessage;

public class SendNetworkBroadcastMessage extends SendRepeatingMessage {

	public SendNetworkBroadcastMessage(int port, String message, String ipAddress) throws SocketException, UnsupportedEncodingException, UnknownHostException {
		super(port, "BROADCAST_ALIVE " + NodeApplication.PROCESS_ID + (NodeApplication.IS_LEADER ? " LEADER" : ""), ipAddress);
		
		// BROADCAST_ALIVE 4849@192.168.10.1
		// BROADCAST_ALIVE 3241@192.168.10.1 LEADER
	}
	
	public static SendNetworkBroadcastMessage create(int port, String MULTICAST_GROUP){
		try {
			SendNetworkBroadcastMessage networkBroadcastThread = new SendNetworkBroadcastMessage(port, null, MULTICAST_GROUP);
			
			return networkBroadcastThread;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
