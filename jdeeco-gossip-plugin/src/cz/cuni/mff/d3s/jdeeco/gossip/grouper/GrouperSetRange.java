/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperSetRange implements GrouperRange {
	
	private Set<Object> range;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.grouper.GrouperRange#inRange(java.lang.Object)
	 */
	@Override
	public boolean inRange(Object key) {
		return this.range.contains(key);
	}
	
	public GrouperSetRange(Collection<Object> range) {
		this.range = new HashSet<Object>(range);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("range: %s", range);
	}
}
