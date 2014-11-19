package advanos.messages.send;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * <p>This class sends a message repeatedly to the destination over UDP on the given port at a certain interval.</p>
 * 
 * <p>Note that the message is sent multiple times. For single messages, see the SendMessage.</p>
 * @author Darren
 * @see SendMessage
 */
public abstract class SendRepeatingMessage extends SendMessage{

	private int msDelay;

	public SendRepeatingMessage(int port, String message, String ipDestination, int msDelay) throws SocketException, UnsupportedEncodingException, UnknownHostException {
		super(port, message, ipDestination);
		this.msDelay = msDelay;
	}
	
	public SendRepeatingMessage(int port, String message, String ipDestination) throws SocketException, UnsupportedEncodingException, UnknownHostException {
		super(port, message, ipDestination);
		this.msDelay = 1000;
	}

	@Override
	public void run() {
		try {
			while(true){
				DatagramPacket packet = new DatagramPacket(data, data.length, destination, port);
				socket.send(packet);
				
				Thread.sleep(msDelay);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			socket.close();
		}
	}
}
