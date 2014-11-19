package advanos.messages.receive;

import java.net.SocketException;

import advanos.NodeApplication;

/**
 * <p>This class handles assignment of the node application and the port to be listened to.</p>
 * 
 * @author Darren
 * @see ReceiveMulticastMessage
 * @see ReceiveSingleMessage
 */
public abstract class ReceiveMessage extends Thread
{
	public static final int MAX_BYTES = 40;
	
	protected NodeApplication nodeApplication;
	protected int port;

	public ReceiveMessage(NodeApplication nodeApplication, int port) throws SocketException {
		this.nodeApplication = nodeApplication;
		this.port = port;
	}
	
	protected abstract void handleMessage(String[] text, String ipAddress);
}
