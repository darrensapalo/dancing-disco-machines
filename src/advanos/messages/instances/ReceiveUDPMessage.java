package advanos.messages.instances;

import java.io.IOException;
import java.net.InetAddress;

import advanos.NodeApplication;
import advanos.messages.receive.ReceiveMessage;

public class ReceiveUDPMessage extends ReceiveMessage
{
	
	public ReceiveUDPMessage(NodeApplication nodeApplication, int port, InetAddress group) throws IOException {
		super(nodeApplication, port, group);
	}

	@Override
	protected void handleMessage(String[] text, String ipAddress) {
		switch (text[0].toUpperCase()) {
		case "CS_REQUEST":
			lamportMutexRequest(text, ipAddress);
			break;
		case "BROADCAST_ALIVE":
			nodeApplication.addDiscoveredHost(ipAddress, text);
			break;
		case "CS_REPLY":
			lamportMutexReply(text, ipAddress);
			break;
		case "ASSIGN":
			nodeApplication.setNextInTokenRing(ipAddress);
			break;
			
		case "SEND":
			nodeApplication.receiveSentToken(text, ipAddress);
			break;
			
		case "RECEIVED":
			nodeApplication.receiveSentTokenConfirmation(text, ipAddress);
			break;
			
		case "REQUEST":
			if (text[1].equalsIgnoreCase("NEXT"))
				nodeApplication.reAssignNextInToken(ipAddress);
		}
	}

	private void lamportMutexReply(String[] text, String ipAddress) {
		
	}

	private void lamportMutexRequest(String[] text, String ipAddress) {
		// update queue in NodeApplication regarding data in text[]
	}

}

