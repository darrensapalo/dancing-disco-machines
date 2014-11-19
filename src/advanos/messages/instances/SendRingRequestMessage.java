package advanos.messages.instances;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;

import advanos.messages.send.SendMessage;

public class SendRingRequestMessage extends SendMessage {

	public SendRingRequestMessage(int port, String message, String ipAddress)
			throws SocketException, UnsupportedEncodingException, UnknownHostException {
		super(port, "REQUEST NEXT", ipAddress);
	}
}
