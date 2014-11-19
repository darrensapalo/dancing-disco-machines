package advanos.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import advanos.Host;

public class DancingGUIFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5657854841509302522L;
	private JLabel lblStatus;
	private DefaultListModel<String> listModel;
	private JList<String> connectedUsers;

	public DancingGUIFrame() {
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
		
		connectedUsers = new JList<String>(listModel);
		JScrollPane scrollpane = new JScrollPane(connectedUsers, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setPreferredSize(new Dimension(300, 220));
		add(scrollpane);

		BoxLayout boxLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		
		setLayout(boxLayout);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		

	}

	public void addUser(Host h) {
		String elem = h.toString();
		if (listModel.contains(elem) == false) {
			listModel.addElement(elem);
			connectedUsers.repaint();
		}
	}
	
	public void removeHost(Host h){
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
		setDanceOnStage(true);
	}
	
	public void stopDance() {
		setDanceOnStage(false);
	}
}
