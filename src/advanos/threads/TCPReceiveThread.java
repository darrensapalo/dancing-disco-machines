package advanos.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import advanos.NodeApplication;

/**
 * This class is for handling TCP socket connections from other systems.
 * @author Darren
 *
 */
public class TCPReceiveThread extends Thread {

	private ServerSocket serverSocket;
	private NodeApplication nodeApplication;
	
	public TCPReceiveThread(NodeApplication nodeApplication, int port) throws IOException  {
			this.nodeApplication = nodeApplication;
			serverSocket = new ServerSocket(port);
			System.out.println(nodeApplication.processID + " has successfully created TCP thread bound at port " + port);
	}
	
	public void run() {
		while (true){
			Socket socket;
			try {
				socket = serverSocket.accept();
				handleConnection(socket);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}	
	}
	
	private void handleConnection(Socket socket){ 
		// if (processing == heavy) open a new thread
		// else do it here
	}

}
