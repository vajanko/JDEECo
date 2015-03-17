/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.Serializable;

/**
 * Structure holding information about message received by some node. These information
 * is populated into the network so that other nodes can know about messages sent
 * by other nodes.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MessageHeader implements Serializable {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -5872849622438169611L;
	
	/**
	 * Unique message ID. For DEECo specific this is the node ID. As a message is
	 * considered the knowledge data.
	 */
	public String id;
	/**
	 * System or simulation time when message {@code id} was received.
	 */
	public Long timestamp;

	/**
	 * Creates a new instance of {@link MessageHeader} with initialised fields
	 */
	public MessageHeader(String id, Long timestamp) {
		this.id = id;
		this.timestamp = timestamp;
	}
}
