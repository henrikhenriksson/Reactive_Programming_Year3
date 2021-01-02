package dt176g.hehe0601.paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;

/**
 * This class represents a line with a start and end point.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2020-12-05
 * @version 3.0
 *
 */

public class Line extends Shape implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8016949982465226644L;

	/**
	 * Initialized constructor called from the super constructor.
	 * 
	 * @param x     the x coordinate to set
	 * @param y     the y coordinate to set
	 * @param color the color to set.
	 */
	public Line(double x, double y, String color, int strokeWidth) {
		super(x, y, color, strokeWidth);
	}

	/**
	 * Initialized constructor called from the super constructor.
	 * 
	 * @param p     Point object holding coordinates to set
	 * @param color the color to set
	 */
	public Line(Point p, String color, int strokeWidth) {
		super(p, color, strokeWidth);
	}

	/**
	 * Method used to draw the shape.
	 * 
	 * @param g, Graphic lib object used to draw the shape.
	 * 
	 */
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Color c = Color.decode(super.color);
		g2d.setStroke(new BasicStroke(strokeWitdh));

		g2d.setColor(c);

		g2d.draw(new Line2D.Double(points.get(0).getX(), points.get(0).getY(), points.get(1).getX(),
				points.get(1).getY()));

	}

	@Override
	public boolean isValidShape() {
		if (!(this.points.size() > 1)) {
			return false;
		}
		return true;
	}
}