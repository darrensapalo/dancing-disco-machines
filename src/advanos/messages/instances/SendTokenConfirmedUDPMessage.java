
package advanos.messages.instances;

import advanos.Host;
import advanos.messages.send.SendUDPMessage;

public class SendTokenConfirmedUDPMessage extends SendUDPMessage {

	public SendTokenConfirmedUDPMessage(Host host) {
		super("RECEIVED TOKEN", host);
	}
}
