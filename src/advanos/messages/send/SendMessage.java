
package advanos.messages.send;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * <p>This class sends a message to the destination over UDP on the given port.</p>
 * 
 * <p>Note that the message is sent only once. For repeating messages, see the SendRepeatingMessage.</p>
 * 
 * @author Darren
 * @see SendRepeatingMessage
 * 
 */
public abstract class SendMessage extends Thread {
	protected DatagramSocket socket;
	protected int port;
	protected byte[] data;

	protected InetAddress destination;

	public SendMessage(int port, String message, String ipAddress) throws SocketException, UnsupportedEncodingException, UnknownHostException {

		this.destination = InetAddress.getByName(ipAddress);
		this.socket = new DatagramSocket(null);
		this.port = port;
		this.data = message.getBytes();

	}

	@Override
	public void run() {
		try {
			DatagramPacket packet = new DatagramPacket(data, data.length, destination, port);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
