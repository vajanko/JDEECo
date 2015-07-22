/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

/**
 * Grouper interval range of integer values.
 *  
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperIntervalRange implements GrouperRange {

	private int from;
	private int to;
	
	/**
	 * Creates a new grouper interval range containing only integers inside the interval.
	 * @param from Begin of the interval
	 * @param to End of the interval
	 */
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
