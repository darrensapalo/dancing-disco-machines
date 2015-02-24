package advanos.messages.receive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import advanos.Host;
import advanos.NodeApplication;

public class TCPSocketHandler extends Thread {
	private Socket socket;
	private PrintWriter printer;
	private NodeApplication node;
	private BufferedReader bufferedReader;
	private boolean isClosed;
	private String ipAddress;
	
	public static TCPSocketHandler create(NodeApplication node, Host host, int port){
		try {
			Socket socket = new Socket(host.getIPAddress(), port);
			TCPSocketHandler tcpSocketHandler = new TCPSocketHandler(socket, node);
			tcpSocketHandler.start();
			return tcpSocketHandler;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public TCPSocketHandler(Socket socket, NodeApplication node){
		this.socket = socket;
		ipAddress = socket.getInetAddress().toString().substring(1);
		this.node = node;
		
		try {
			InputStream inputStream = socket.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		
			OutputStream outputStream = socket.getOutputStream();
			printer = new PrintWriter(outputStream, true);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void run() {
		while(isClosed == false){
			String line;
			try {
				while(isClosed == false && (line = bufferedReader.readLine()) != null){
					String[] text = line.split(" ");
					String ipAddress = socket.getInetAddress().toString().substring(1);
					System.out.println("received message: " + line + " from " + node.getHost(ipAddress));
					handleMessage(text, ipAddress);
				}
			} catch (IOException e) {
				System.err.println("The connection suddenly failed.");
				isClosed = true;
				node.removeHost(ipAddress);
			}
		}
		try {
			System.err.println("Closing the socket.");
			socket.close();	
			System.err.println("Socket is closed.");
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public synchronized void sendMessage(String message){
		System.out.println("Sending message: " + message);
		printer.println(message);
	}
	
	public void handleMessage(String[] text, String ipAddress) {
		switch (text[0].toUpperCase()) {
		case "ASSIGN":
			if (text[1].equalsIgnoreCase("NEXT")){
				while(node.getHost(text[2]) == null)
				{
					try {
						// Try to wait for the host to be identified
						Thread.sleep(1000);
						System.err.println("I still don't know this host. I'll wait for a second until I discover it.");
					}catch(Exception e){
						
					}
				}
				node.createNextTCPconnection(text[2]);
				if (NodeApplication.TOKEN){
					node.attemptToDance();
				}
			}
			
			break;

		case "SEND":
			if (text[1].equalsIgnoreCase("TOKEN"))
				node.confirmReceiptOfToken(ipAddress);
			break;

		case "RECEIVED":
			if (text[1].equalsIgnoreCase("TOKEN"))
				node.receiveSentTokenConfirmation(text, ipAddress);
			break;

		case "REQUEST":
			if (text[1].equalsIgnoreCase("NEXT"))
				node.reAssignNextInToken(ipAddress);
			break;
		case "CLOSE":
				node.removeHost(ipAddress);
			break;
		}
	}

	public void close() {
		try {
			sendMessage("CLOSE");
		} catch (Exception e) {
			System.out.println("Could not close inform other host that connection is being closed.");
		}
		
		isClosed = true;
	}
}
