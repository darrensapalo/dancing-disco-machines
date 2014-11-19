package advanos.messages.receive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import advanos.NodeApplication;

/**
 * <p>This class handles receiving a single message. This does not register to an InetAddress group.</p>
 * 
 * <p> To receive multicast broadcasts over UDP, see ReceiveMulticastMessage.</p>
 * 
 * @author Darren
 * @see ReceiveMulticastMessage
 */
public abstract class ReceiveSingleMessage extends ReceiveMessage
{
	protected DatagramSocket socket;

	public ReceiveSingleMessage(NodeApplication nodeApplication, int port) throws SocketException {
		super(nodeApplication, port);
		
		socket = new DatagramSocket(port);
	}

	public void run() {
		DatagramPacket packet = new DatagramPacket(new byte[MAX_BYTES], MAX_BYTES);

		while (true) {
			try {
				socket.receive(packet);
				String message = new String(packet.getData(), "UTF-8");
				String[] text = message.split(" ");
				handleMessage(text, packet.getAddress().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
