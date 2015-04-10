/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

/**
 * Structure for representing GPS position.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class Position {
	public double x;
	public double y;
	
	public Position() {
		this(0, 0);
	}
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return String.format("[%f, %f]", x, y);
	};

}
