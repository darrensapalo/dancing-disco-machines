package advanos;


public class Driver {
	
	public static void main(String[] args) {
		String isLeader = args[0];
		boolean isLead = isLeader.equals("true");
		new NodeApplication(4040, isLead);
	}
}
