/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.buffer;

import java.io.Serializable;

/**
 * Structure holding information about item received by some node. These information
 * is populated into the network so that other nodes can know about messages sent
 * by other nodes.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ItemHeader implements Serializable {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -6503219299056478709L;

	/**
	 * Unique item identifier.
	 */
	public String id;
	/**
	 * System or simulation time when message {@code id} was received.
	 */
	public Long timestamp;
	
	/**
	 * Creates a new instance of {@link ItemHeader} with initialised fields
	 */
	public ItemHeader(String id, Long timestamp) {
		this.id = id;
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return String.format("%s-%d", id, timestamp);
	}
}
