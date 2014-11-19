package advanos.messages.receive;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import advanos.Host;
import advanos.NodeApplication;

public class TCPServer extends Thread {
	private NodeApplication node;
	private ServerSocket socket;

	private HashMap<String, TCPSocketHandler> hashmap = new HashMap<String, TCPSocketHandler>();
	private static int port;
	
	public static TCPServer create(NodeApplication node, int port){
		TCPServer.port = port;
		
		try {
			return new TCPServer(node, port);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public TCPServer(NodeApplication node, int port) throws IOException {
		this.node = node;
		socket = new ServerSocket(port);
	}

	public void run() {
		while (true) {
			try {
				Socket newSocket = socket.accept();
				TCPSocketHandler handler = new TCPSocketHandler(newSocket, node);
				hashmap.put(newSocket.getInetAddress().toString().substring(1), handler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public TCPSocketHandler getTCPSocketHandler(Host h) {
		String ipAddress = h.getIPAddress();
		if (hashmap.containsKey(ipAddress)){
			return hashmap.get(ipAddress);
		}
		else
			return TCPSocketHandler.create(node, h, port);
	}


}
