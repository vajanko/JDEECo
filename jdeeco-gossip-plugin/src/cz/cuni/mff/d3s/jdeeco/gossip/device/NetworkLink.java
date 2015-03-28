/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.device;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class NetworkLink {
	
	public int node1;
	public int node2;
	
	public NetworkLink(int node1, int node2) {
		this.node1 = node1;
		this.node2 = node2;
	}
}
