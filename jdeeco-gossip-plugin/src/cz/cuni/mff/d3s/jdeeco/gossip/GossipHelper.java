/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Random;

/**
 * Common functionality necessary for the gossip protocol used by several
 * plugins.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GossipHelper {
	/**
	 * Random generator used by the gossip protocol modules.
	 */
	public static final Random generator = new Random(123);
}
