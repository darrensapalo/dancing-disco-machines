package advanos.messages.receive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
	protected MulticastSocket socket;	
	protected int port;


	public ReceiveMessage(NodeApplication nodeApplication, int port) throws IOException{
		this(nodeApplication, port, null);
	}
	
	public ReceiveMessage(NodeApplication nodeApplication, int port, InetAddress group) throws IOException {
		this.nodeApplication = nodeApplication;
		this.port = port;

		socket = new MulticastSocket(port);
		
		if (group != null)
			socket.joinGroup(group);
	}
	
	
	public void run() {
		DatagramPacket packet = new DatagramPacket(new byte[MAX_BYTES], MAX_BYTES);

		while (true) {
			try {
				socket.receive(packet);
				String message = new String(packet.getData(), "UTF-8");
				String[] text = message.split(" ");
				handleMessage(text, packet.getAddress().toString().substring(1));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void handleMessage(String[] text, String ipAddress);
}
