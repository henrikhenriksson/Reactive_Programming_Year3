package dt176g.hehe0601.paint;

import java.io.Serializable;

/**
 * Represents a two-dimensional point. The class is used in different geometric
 * classes. Contains information about the x and y coordinates of the point.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2019-11-11
 * @version 1.1
 * 
 */

public class Point implements Serializable {

	private static final long serialVersionUID = 7481789027237029660L;

	private double x;

	private double y;

	/**
	 * default constructor, sets the x,y coordinates to 0.0
	 */
	public Point() {
		x = 0;
		y = 0;
	}

	/**
	 * Initialized constructor, setting the x,y coordinates to input values.
	 * 
	 * @param x, the x to set
	 * @param y, the y to set
	 */
	public Point(double x, double y) {

		this.x = x;
		this.y = y;

	}

	/**
	 * Getter for the X coordinate
	 * 
	 * @return the x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Setter for the X coordinate
	 * 
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Getter for the Y coordinate
	 * 
	 * @return the y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Setter for the Y coordinate
	 * 
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * print information about the class object coordinates.
	 * 
	 * @return string holding current coordinate x and y values.
	 * @Override
	 */
	public String toString() {

		return "[" + x + "," + y + "]";
	}

}
