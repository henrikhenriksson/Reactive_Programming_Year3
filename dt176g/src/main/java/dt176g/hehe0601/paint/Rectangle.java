package dt176g.hehe0601.paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * This class represents a rectangle. A rectangle has a starting point (its
 * upper left corner) and an end point (its lower right corner). From these
 * points, the rectangle's width, height, circumstance and area can be
 * calculated.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2019-11-11
 * @version 3.0
 *
 */

public class Rectangle extends Shape implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1241314850860049782L;

	/**
	 * Initialized constructor called from the super constructor.
	 * 
	 * @param x     the x coordinate to set
	 * @param y     the y coordinate to set
	 * @param color the color to set.
	 */
	public Rectangle(double x, double y, String color, int strokeWidth) {
		super(x, y, color, strokeWidth);
	}

	/**
	 * Initialized constructor called from the super constructor.
	 * 
	 * @param p     Point object holding coordinates to set
	 * @param color the color to set
	 */
	public Rectangle(Point p, String color, int strokeWidth) {
		super(p, color, strokeWidth);
	}

	/**
	 * Method used to return the width of a circle object based on its start and end
	 * point x coordinates.
	 * 
	 * @return double, width of the rectangle.
	 */
	public double getWidth() throws NoEndPointException {

		if (!(super.points.size() < 2)) {
			return Math.abs(points.get(1).getX() - points.get(0).getX());
		} else {
			throw new NoEndPointException("Rectangle width could not be calculated. No end Point Exception.");
		}
	}

	/**
	 * Method used to return the height of a circle object based on its start and
	 * end point y coordinates.
	 * 
	 * @return double, height of the rectangle.
	 */
	public double getHeight() throws NoEndPointException {

		if (!(super.points.size() < 2)) {
			return Math.abs(points.get(1).getY() - points.get(0).getY());
		} else {
			throw new NoEndPointException("Rectangle Height could not be calculated. No end Point Exception.");
		}
	}

	/**
	 * Method used to draw the shape.
	 * 
	 * @param g, Graphic lib object used to draw the shape.
	 * 
	 */
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		try {

			Color c = Color.decode(super.color);
			g2d.setStroke(new BasicStroke(strokeWitdh));

			g2d.setColor(c);
			// To enable drawing in any direction.
			Double x = points.get(0).getX() < points.get(1).getX() ? points.get(0).getX()
					: points.get(0).getX() - getWidth();
			Double y = points.get(0).getY() < points.get(1).getY() ? points.get(0).getY()
					: points.get(0).getY() - getHeight();

			g2d.draw(new Rectangle2D.Double(x, y, getWidth(), getHeight()));

		} catch (NoEndPointException | IndexOutOfBoundsException e) {
			System.err.println("Error drawing shape." + e.getMessage());
		}

	}

	@Override
	// make sure an endpoint exists.
	public boolean isValidShape() {
		if (!(this.points.size() > 1)) {
			return false;
		}
		return true;
	}
}