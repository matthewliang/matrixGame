

// imports necessary libraries for Java swing
import java.awt.*;
import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
	public void run() {

		// Top-level frame in which game components live
		final JFrame frame = new JFrame("The Matrix");
		frame.setBackground(Color.WHITE);
		frame.setLocation(600, 300);

		// Main playing area
		final GameCourt court = new GameCourt();
		frame.add(court, BorderLayout.CENTER);
		
		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		court.showMenu();
	}

	/*
	 * Main method run to start and run the game Initializes the GUI elements
	 * specified in Game and runs it IMPORTANT: Do NOT delete! You MUST include
	 * this in the final submission of your game.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}
