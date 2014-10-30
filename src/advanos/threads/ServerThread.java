package advanos.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

	private ServerSocket serverSocket;

	public ServerThread(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		while (true){
			Socket socket = serverSocket.accept();
			handleConnection(socket);
		}
	}
	
	private void handleConnection(Socket socket){ 
		// if (processing == heavy) open a new thread
		// else do it here
	}

}
