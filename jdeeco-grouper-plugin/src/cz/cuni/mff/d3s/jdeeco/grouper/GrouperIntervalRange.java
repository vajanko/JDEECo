/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperIntervalRange implements GrouperRange {

	private int from;
	private int to;
	
	public GrouperIntervalRange(int from, int to) {
		this.from = from;
		this.to = to;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.grouper.GrouperRange#inRange(java.lang.Object)
	 */
	@Override
	public boolean inRange(Object key) {
		if (!(key instanceof Integer))
			return false;
		
		Integer sector = (Integer)key;
		
		return from <= sector && sector <= to;
	}

}
