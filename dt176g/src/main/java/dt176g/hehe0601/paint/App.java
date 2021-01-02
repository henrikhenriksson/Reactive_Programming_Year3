package dt176g.hehe0601.paint;

/**
 * @file App.java
 * @brief Main class starting the paint application on a new thread.
 * @author Henrik Henriksson (hehe0601)
 * @since 2020-01-02
 * @version 3.0
 *
 */

import javax.swing.SwingUtilities;

public class App {
	public String getGreeting() {
		return "Hello world.";
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MyJFrame().setVisible(true));
	}
}
