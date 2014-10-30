package advanos;

import java.io.IOException;

import advanos.gui.DancingGUIFrame;
import advanos.threads.ServerThread;

public class NodeApplication {
	private ServerThread serverThread;
	private DancingGUIFrame gui;
	
	public NodeApplication(int port) {
		gui = new DancingGUIFrame();
		gui.setVisible(true);
		
		// Get a server thread up and running
		do {
			try {
				serverThread = new ServerThread(port);
				serverThread.start();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} while (serverThread == null);
		
		
	}
}
