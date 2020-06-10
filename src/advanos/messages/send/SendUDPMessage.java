
package advanos.messages.send;

import advanos.Host;
import advanos.messages.Messager;
import io.reactivex.Observable;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * <p>This class sends a message to the destination over UDP on the given port.</p>
 * 
 * <p>Note that the message is sent only once. For repeating messages, see the SendRepeatingMessage.</p>
 * 
 * @author Darren
 * @see SendRepeatingMessage
 * 
 */
public class SendUDPMessage implements Messager<Boolean> {
	private final Host host;
	protected byte[] data;

	public SendUDPMessage(String message, Host host) {
		this.host = host;
		this.data = message.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public Observable<Boolean> perform() {
		return Observable.create(subscriber -> {
			try {
				DatagramSocket socket = new DatagramSocket(null);
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(this.host.getIpAddress()), this.host.getUDPPort());

				socket.send(packet);
				socket.close();

				subscriber.onNext(true);
				subscriber.onComplete();
			} catch (Exception e) {
				subscriber.onError(e);
			}
		});
	}

	public void run() {

	}
}
