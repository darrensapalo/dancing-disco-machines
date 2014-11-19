package advanos.messages.instances;

import java.io.IOException;
import java.net.InetAddress;

import advanos.NodeApplication;
import advanos.messages.receive.ReceiveMessage;

public class ReceiveUDPMessage extends ReceiveMessage
{
	public static ReceiveUDPMessage create(NodeApplication node, int port, String group){
		try {
			ReceiveUDPMessage receiveUDPThread = new ReceiveUDPMessage(node, port, InetAddress.getByName(group));
			
			return receiveUDPThread;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public ReceiveUDPMessage(NodeApplication nodeApplication, int port, InetAddress group) throws IOException {
		super(nodeApplication, port, group);
	}

	@Override
	protected void handleMessage(String[] text, String ipAddress) {
		switch (text[0].toUpperCase()) {
		case "BROADCAST_ALIVE":
			nodeApplication.addDiscoveredHost(ipAddress, text);
			break;
		}
	}
}

