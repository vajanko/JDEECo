/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperAreaRange implements GrouperRange {

	private double top;
	private double bottom;
	private double right;
	private double left;
	
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
