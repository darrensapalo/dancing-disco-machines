package advanos.threads;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkBroadcastThread extends Thread {

	private int port;
	private byte[] data;

	public NetworkBroadcastThread(int port, byte[] data) {
		this.port = port;
		this.data = data;
	}

	@Override
	public void run() {
		try {
			
		while (true) {
			try {
				InetAddress host = InetAddress.getByName("localhost");
				DatagramSocket socket = new DatagramSocket(null);
				DatagramPacket packet = new DatagramPacket(data, 0, host, port);
				System.out.println("Sending...");
				packet.setLength(data.length);
				socket.send(packet);
				socket.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.sleep(500);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
