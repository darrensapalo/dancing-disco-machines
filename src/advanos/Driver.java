package advanos;

import java.net.UnknownHostException;


public class Driver {
	
	public static void main(String[] args) throws UnknownHostException {
		NodeApplication.MULTICAST_GROUP = "192.168.10.0";
		
		String isLeader = args[0];
		boolean isLead = isLeader.equals("true");
		
		new NodeApplication(4040, isLead);
	}
}
