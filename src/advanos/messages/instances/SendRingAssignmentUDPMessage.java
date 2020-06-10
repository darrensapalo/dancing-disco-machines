package advanos.messages.instances;

import advanos.Host;
import advanos.messages.send.SendUDPMessage;

public class SendRingAssignmentUDPMessage extends SendUDPMessage {

	public SendRingAssignmentUDPMessage(String ipAddress, Host host) {
		super(String.format("ASSIGN %s", ipAddress), host);
	}
}
