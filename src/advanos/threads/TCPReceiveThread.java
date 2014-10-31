package advanos.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is for handling TCP socket connections from other systems.
 * @author Darren
 *
 */
public class TCPReceiveThread extends Thread {

	private ServerSocket serverSocket;
	
	public TCPReceiveThread(int port) throws IOException {
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
