package advanos;

public class Host {
	private int TCPPort;
	private int UDPPort;
	private String ipAddress;
	private String processID;
	private boolean isLeader;

	public Host(String ipAddress, String processID){
		this.ipAddress = ipAddress;
		this.processID = processID;
		this.isLeader = false;
	}
	
	@Override
	public String toString() {
		return ipAddress + ":" + processID;
	}

	public int getTCPPort() {
		return TCPPort;
	}

	public void setTCPPort(int tCPPort) {
		TCPPort = tCPPort;
	}

	public int getUDPPort() {
		return UDPPort;
	}

	public void setUDPPort(int uDPPort) {
		UDPPort = uDPPort;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}
	
	public boolean equals(Object obj) {
		return obj instanceof Host && obj.toString().equals(toString());
	}

	public boolean isLeader() {
		return isLeader;
	}

	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}

	public String getIPAddress() {
		return ipAddress;
	}
}
