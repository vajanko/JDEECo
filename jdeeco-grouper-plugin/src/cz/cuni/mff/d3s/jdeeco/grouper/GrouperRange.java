/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents grouper range of keys for which is the grouper responsible.
 * Current implementation is an enumeration of keys but may be changed to
 * reflect ranges of numbers and to spare memory.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperRange {
	
	private Set<Object> range;
	
	public boolean inRange(Object key) {
		return this.range.contains(key);
	}
	
	public GrouperRange(Collection<Object> range) {
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
