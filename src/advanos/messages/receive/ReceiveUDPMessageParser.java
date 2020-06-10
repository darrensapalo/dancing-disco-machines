package advanos.messages.receive;

import advanos.Host;
import advanos.messages.Message;
import advanos.messages.Messager;
import io.reactivex.Observable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

/**
 * <p>This class handles assignment of the node application and the port to be listened to.</p>
 * 
 * @author Darren
 */
public class ReceiveUDPMessageParser implements Messager<Message>
{
	public static final int MAX_BYTES = 40;
	private final InetAddress group;

	protected MulticastSocket multicastSocket;
	protected int port;

	private Boolean isDisposed = false;

	public ReceiveUDPMessageParser(int port, InetAddress group)  {
		this.port = port;
		this.group = group;
	}

	@Override
	public Observable<Message> perform() {
		return Observable.create(subscriber -> {
			try {
				multicastSocket = new MulticastSocket(port);

				if (group != null)
					multicastSocket.joinGroup(this.group);

				DatagramPacket packet = new DatagramPacket(new byte[MAX_BYTES], MAX_BYTES);

				while (!isDisposed) {
					try {
						multicastSocket.receive(packet);
						String content = new String(packet.getData(), StandardCharsets.UTF_8);

						Message message = Message.builder()
								.message(content)
								.source(Host.builder()
									.ipAddress(packet.getAddress().getHostAddress())
									.build())
								.build();

						subscriber.onNext(message);
					} catch (IOException e) {
						subscriber.onError(e);
					}
				}
				subscriber.onComplete();
			} catch (Exception e) {
				subscriber.onError(e);
			}

		});
	}

	public void dispose() {
		multicastSocket.disconnect();
		isDisposed = true;
	}
}
