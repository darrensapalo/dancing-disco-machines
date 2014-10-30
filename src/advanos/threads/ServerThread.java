package advanos.threads;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerThread extends Thread {

	private ServerSocket serverSocket;

	public ServerThread(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

}
