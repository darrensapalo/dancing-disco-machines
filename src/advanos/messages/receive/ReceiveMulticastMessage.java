package advanos.messages.receive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import advanos.NodeApplication;

/**
 * <p>This class handles receiving a message that was broadcasted to a certain InetAddress group.</p>
 * 
 * <p> To receive a single message over UDP, see ReceiveSingleMessage.</p>
 * 
 * @author Darren
 * @see ReceiveSingleMessage
 */
public abstract class ReceiveMulticastMessage extends ReceiveMessage {
	
	protected MulticastSocket socket;	
	public ReceiveMulticastMessage(NodeApplication nodeApplication, int port, InetAddress group)throws IOException {
		super(nodeApplication, port);
		socket = new MulticastSocket(port);
		socket.joinGroup(group);
	}
	
	public void run() {
		DatagramPacket packet = new DatagramPacket(new byte[MAX_BYTES], MAX_BYTES);

		while (true) {
			try {
				socket.receive(packet);
				String message = new String(packet.getData());
				String[] text = message.split(" ");
				handleMessage(text, packet.getAddress().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
