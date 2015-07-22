/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

/**
 * The role of the grouper plugin. There is a special plugin at the client side
 * allowing the node to receive group membership notifications from the server. 
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public enum GrouperRole {
	none,
	/**
	 * Regular node which is capable of membership reception from server node.
	 */
	client,
	/**
	 * Grouper node performing evaluation of partitioning function on received knowledge.
	 */
	server,
}
