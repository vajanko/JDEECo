/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperRadiusRange implements GrouperRange {

	private Position position;
	private double radius;
	
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
	
	private static double distance(Position a, Position b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

}
