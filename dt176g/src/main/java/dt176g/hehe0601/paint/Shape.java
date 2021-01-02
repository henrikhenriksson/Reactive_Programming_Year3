package dt176g.hehe0601.paint;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class will be a super class for different geometric shapes (like
 * rectangle and circle). The main class "Shape" is abstract.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2019-11-11
 * @version 3.0
 *
 */

abstract public class Shape implements IDrawable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 367599657908665317L;

	protected String color; /// < color, the color of the shape

	protected int strokeWitdh;

	protected ArrayList<Point> points; /// < an array of points.

	/**
	 * default constructor used by JAXB
	 */
	public Shape() {
		this.color = "";
		this.strokeWitdh = 2;
		this.points = new ArrayList<Point>();
	}

	/**
	 * Initialized constructor using individual x and y coordinates. Calls the
	 * second constructor.
	 * 
	 * @param x     the x coordinate to set
	 * @param y     the y coordinate to set
	 * @param color the color to set
	 */
	public Shape(double x, double y, String color, int size) {
		this(new Point(x, y), color, size);
	}

	/**
	 * Initialized constructor using a Point object to set x,y coordinates.
	 * 
	 * @param p     Point object holding coordinates to set
	 * @param color the color to set.
	 */
	public Shape(Point p, String color, int size) {
		this.points = new ArrayList<Point>();
		this.points.add(0, p);
		this.color = color;
		this.strokeWitdh = size;
	}

	/**
	 * Method used to get the color of the shape.
	 * 
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Method used to set the color of the shape.
	 * 
	 * @param color, the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * abstract method to be implemented in extended classes.
	 * 
	 * @param g, a Graphics lib object used to draw the shape.
	 */
	public abstract void draw(java.awt.Graphics g);

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void addPoint(Point p) {
		this.points.add(p);
	}

	public void addPoint(double x, double y) {
		this.points.add(new Point(x, y));

	}

	public void setPoint(Point p) {
		if (points.size() > 1) {
			this.points.set(1, p);
		} else {
			this.points.add(1, p);
		}
	}

	public void setPoint(double x, double y) {
		this.setPoint(new Point(x, y));
	}

	public abstract boolean isValidShape();
}