package advanos.messages.instances;

import advanos.Host;
import advanos.messages.send.SendUDPMessage;

public class SendTokenUDPMessage extends SendUDPMessage {

	public SendTokenUDPMessage(Host host) {
		super("SEND TOKEN", host);
	}
}
