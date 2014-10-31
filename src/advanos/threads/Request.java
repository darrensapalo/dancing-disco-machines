package advanos.threads;

/**
 * This class is used for synchronization between
 * distributed systems. A Request class contains the
 * identifier (process id + IP address) and its
 * Lamport timestamp.
 * 
 * See also: http://en.wikipedia.org/wiki/Lamport_timestamps
 * https://www.youtube.com/watch?v=r7SJOhGF4Nc
 * @author Darren
 *
 */
public class Request {
	private int timestamp;
	private String uniqueProcessID;

	public Request(String uniqueProcessID, int timestamp) {
		this.uniqueProcessID = uniqueProcessID;
		this.timestamp = timestamp;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public String getIdentifier() {
		return uniqueProcessID;
	}
	
	
}
