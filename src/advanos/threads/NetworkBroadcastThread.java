package advanos.threads;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkBroadcastThread extends Thread {

	private int port;
	private byte[] data;
	private int max;
	private int offset;

	public NetworkBroadcastThread(int port, byte[] data) {
		this.port = port;
		this.max = 16;
		this.offset = 0;
		
		this.data = data;
	}

	@Override
	public void run() {
		try {
			int port = this.port;
			while (true) {
				port = this.port + (offset % max);
				try {
					InetAddress host = InetAddress.getByName("localhost");
					DatagramSocket socket = new DatagramSocket(null);
					DatagramPacket packet = new DatagramPacket(data, 0, host,
							port);
					packet.setLength(data.length);
					socket.send(packet);
					socket.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(300);
				offset++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
