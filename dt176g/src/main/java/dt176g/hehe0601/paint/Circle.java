package dt176g.hehe0601.paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

/**
 * This class represents a circle. A circle has a starting point (its center)
 * and an end point (its outer edge). Based on these points, the radius,
 * circumstance and area of the circle can be calculated.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2019-11-11
 * @version 4.0
 *
 */

public class Circle extends Shape implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 398917395771507782L;

	/**
	 * Initialized constructor called from the super constructor.
	 * 
	 * @param x     the x coordinate to set
	 * @param y     the y coordinate to set
	 * @param color the color to set.
	 */
	public Circle(double x, double y, String color, int strokeWidth) {
		super(x, y, color, strokeWidth);

	}

	/**
	 * Initialized constructor called from the super constructor.
	 * 
	 * @param p     Point object holding coordinates to set
	 * @param color the color to set
	 */
	public Circle(Point p, String color, int strokeWidth) {
		super(p, color, strokeWidth);

	}

	/**
	 * method used to calculate the radius of a circle based on its start and end
	 * points. The Pythagoran theorem is used to calculate the radius.
	 * 
	 * @return double, the radius of the circle shape. -1 if no calculation was
	 *         possible.
	 * @throws NoEndPointException
	 */
	public double getRadius() throws NoEndPointException {

		if (!(super.points.size() < 2)) {

			double px = super.points.get(1).getX() - super.points.get(0).getX();
			double py = super.points.get(1).getY() - super.points.get(0).getY();

			return Math.sqrt((Math.pow(px, 2) + Math.pow(py, 2)));
		} else {
			throw new NoEndPointException("Circle radius could not be calculated. No end Point Exception.");
		}
	}

	/**
	 * Method used to draw the shape. Currently only prints "this" object.
	 * 
	 * @param g, Graphic lib object used to draw the shape.
	 */
	@Override
	public void draw(java.awt.Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Color c = Color.decode(super.color);

		g2d.setColor(c);
		g2d.setStroke(new BasicStroke(strokeWitdh));
		try {

			g2d.draw(new Ellipse2D.Double((super.points.get(0).getX() - getRadius()),
					(super.points.get(0).getY() - getRadius()), (getRadius() * 2), (getRadius() * 2)));

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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