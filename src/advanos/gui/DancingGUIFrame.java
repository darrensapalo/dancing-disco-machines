package advanos.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

public class DancingGUIFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5657854841509302522L;

	public DancingGUIFrame() {
		super("Disco machine");
		setPreferredSize(new Dimension(250, 300));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}
}
