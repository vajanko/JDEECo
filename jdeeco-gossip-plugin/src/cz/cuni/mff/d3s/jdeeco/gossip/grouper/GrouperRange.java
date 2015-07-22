/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

/**
 * Represents grouper range of keys for which is the grouper responsible.
 * Current implementation is an enumeration of keys but may be changed to
 * reflect ranges of numbers and to spare memory.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public interface GrouperRange {
	/**
	 * Gets value indicating whether given value is inside the range.
	 * 
	 * @param key Value from a specific domain.
	 * @return True if provided value is from the range otherwise false.
	 */
	public boolean inRange(Object key);
}
