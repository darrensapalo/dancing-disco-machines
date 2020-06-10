package advanos.messages.send;

import advanos.Host;
import advanos.messages.Messager;
import io.reactivex.Observable;

/**
 * <p>This class sends a message repeatedly to the destination over UDP on the given port at a certain interval.</p>
 * 
 * <p>Note that the message is sent multiple times. For single messages, see the SendUDPMessage.</p>
 * @author Darren
 * @see SendUDPMessage
 */
public abstract class SendRepeatingMessage implements Messager<Boolean> {

	/**
	 * How long until the next UDP packet is sent out.
	 */
	public static final int DELAY_MS = 1000;
	private final String message;
	private final Host host;

	public SendRepeatingMessage(String message, Host host)  {
		this.message = message;
		this.host = host;

	}

	@Override
	public Observable<Boolean> perform() {
		return new SendUDPMessage(this.message, this.host).perform()
			.repeat(DELAY_MS);
	}
}
