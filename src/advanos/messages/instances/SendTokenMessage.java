package advanos.messages.instances;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;

import advanos.messages.send.SendMessage;

public class SendTokenMessage extends SendMessage {

	public SendTokenMessage(int port, String message, String ipAddress)
			throws SocketException, UnsupportedEncodingException, UnknownHostException {
		super(port, "SEND TOKEN", ipAddress);
	}
}
