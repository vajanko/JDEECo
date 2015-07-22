/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * Grouper circle range of positioning values.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperRadiusRange implements GrouperRange {

	// circle centre position
	private Position position;
	// circle radius
	private double radius;
	
	/**
	 * Creates a new grouper circle range containing only positions inside the circle.
	 * 
	 * @param position Position of the circle centre
	 * @param radius Circle radius
	 */
	public GrouperRadiusRange(Position position, double radius) {
		this.position = position;
		this.radius = radius;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.grouper.GrouperRange#inRange(java.lang.Object)
	 */
	@Override
	public boolean inRange(Object key) {
		if (!(key instanceof Position))
			return false;
		
		Position pos = (Position)key;
		return distance(pos, this.position) <= this.radius;
	}
	
	/**
	 * Calculates distance between to points.
	 * 
	 * @param a Begin position
	 * @param b End position
	 * @return Distance between two points.
	 */
	private static double distance(Position a, Position b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

}
