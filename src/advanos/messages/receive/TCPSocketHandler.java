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
		while(true){
			String line;
			try {
				while((line = bufferedReader.readLine()) != null){
					String[] text = line.split(" ");
					String ipAddress = socket.getInetAddress().toString().substring(1);
					System.out.println("received message: " + line);
					handleMessage(text, ipAddress);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void sendMessage(String message){
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
						System.err.println("I still don't know this host. Please wait until I discover it.");
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
		}
	}
}
