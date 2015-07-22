/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * Grouper rectangle range of positioning values. 
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperAreaRange implements GrouperRange {

	private double top;
	private double bottom;
	private double right;
	private double left;
	
	/**
	 * Creates a new grouper rectangle range containing only positions inside the rectangle.
	 * 
	 * @param top Top coordinate of the rectangle range
	 * @param bottom Bottom coordinate of the rectangle range
	 * @param left Left coordinate of the rectangle range
	 * @param right Right coordinate of the rectangle range
	 */
	public GrouperAreaRange(double top, double bottom, double left, double right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.grouper.GrouperRange#inRange(java.lang.Object)
	 */
	@Override
	public boolean inRange(Object key) {
		if (!(key instanceof Position))
			return false;
		
		Position pos = (Position)key;
		
		return left <= pos.x && pos.x <= right
				&& bottom <= pos.y && pos.y <= top;
	}

}
