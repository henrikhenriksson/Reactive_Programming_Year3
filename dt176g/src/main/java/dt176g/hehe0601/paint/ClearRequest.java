package dt176g.hehe0601.paint;

import java.awt.Graphics;
import java.io.Serializable;

/**
 * 
 * This class handles clear requests from the user. It calls the "Clear" method
 * instead of draw.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2020-01-02
 * @version 3.0
 *
 */

public class ClearRequest extends Shape implements Serializable {

	private static final long serialVersionUID = 8237060949668102235L;

	@Override
	public String toString() {
		System.out.println("this");
		return null;
	}

	@Override
	public void draw(Graphics g) {
		System.out.println("Clear everything command sent");
	}

	// The clear method draws a rectangle of the background color covering the
	// entire drawingpanel.
	public void clear(Graphics g, int width, int height) {
		g.clearRect(0, 0, width, height);
	}

	@Override
	// will always return true, as it will otherwise be discarded as an incomplete
	// shape "missing an endpoint"
	public boolean isValidShape() {
		return true;
	}
}
