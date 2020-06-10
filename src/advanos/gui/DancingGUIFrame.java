package advanos.gui;

import advanos.ApplicationState;
import advanos.Host;
import io.reactivex.subjects.BehaviorSubject;

import javax.swing.*;
import java.awt.*;

public class DancingGUIFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5657854841509302522L;
	private JLabel lblStatus;
	private DefaultListModel<String> listModel;
	private JList<String> connectedUsers;

	public DancingGUIFrame(BehaviorSubject<ApplicationState> applicationState) {
		super("Disco machine");
		setPreferredSize(new Dimension(450, 400));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		lblStatus = new JLabel();
		lblStatus.setPreferredSize(new Dimension(400, 25));
		setDanceOnStage(false);

		add(lblStatus);
		
		JLabel lblUsers = new JLabel("Connected users");
		lblUsers.setPreferredSize(new Dimension(400, 25));
		add(lblUsers);
		
		listModel = new DefaultListModel<String>();

		applicationState.map(ApplicationState::getHosts)
			.subscribe(hosts -> {
				listModel.clear();
				hosts.stream()
					.map(Host::getProcessID)
					.forEach(host -> listModel.addElement(host));
			});
		
		connectedUsers = new JList<String>(listModel);
		JScrollPane scrollpane = new JScrollPane(connectedUsers, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setPreferredSize(new Dimension(300, 220));
		add(scrollpane);

		BoxLayout boxLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		
		setLayout(boxLayout);
		pack();
		setLocationRelativeTo(null);
	}

	public void addUser(Host h) {
		String elem = h.toString();
		if (!listModel.contains(elem)) {
			listModel.addElement(elem);
			connectedUsers.repaint();
		}
	}
	
	public void removeUser(Host h){
		String elem = h.toString();
		listModel.removeElement(elem);
	}

	public void setDanceOnStage(boolean b) {
		if (b)
			lblStatus.setText("Status: On stage");
		else
			lblStatus.setText("Status: Dancing individually");
	}

	public void dance() {
		System.err.println("I'm dancing!");
		System.out.println();
		
	}
}
