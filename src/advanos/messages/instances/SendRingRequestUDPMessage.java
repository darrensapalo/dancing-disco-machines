package advanos.messages.instances;

import advanos.Host;
import advanos.messages.send.SendUDPMessage;

public class SendRingRequestUDPMessage extends SendUDPMessage {

	public SendRingRequestUDPMessage(Host host) {
		super( "REQUEST NEXT", host);
	}
}
