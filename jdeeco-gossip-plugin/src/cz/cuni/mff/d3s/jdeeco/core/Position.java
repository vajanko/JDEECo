/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import java.io.Serializable;

/**
 * Structure for representing GPS position.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class Position implements Serializable {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 1504628624138064939L;
	
	/**
	 * X-coordinate of GPS position
	 */
	public double x;
	/**
	 * Y-coordinate of GPS position
	 */
	public double y;
	
	/**
	 * Creates [0, 0] GPS position
	 */
	public Position() {
		this(0, 0);
	}
	/**
	 * Creates position with initialised fields
	 * 
	 * @param x coordinate of GPS position
	 * @param y coordinate of GPS position
	 */
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return String.format("[%.2f, %.2f]", x, y);
	};

}
