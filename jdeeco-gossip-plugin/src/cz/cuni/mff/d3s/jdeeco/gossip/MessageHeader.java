/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Wraps a collection of message headers received by particular node.
 * Each header is associated with a time instance when message was received.
 * These data are used by nodes to discover that some specific message is 
 * missing on the current node or to request a particular message rebroadcast.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class MessageHeader implements Serializable {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 1231352417793958761L;

	// a collection of headers of messages
	private Collection<ItemHeader> headers;
	
	/**
	 * Gets the collection of headers for each message received by the node.
	 * 
	 * @return Collection of message headers.
	 */
	public Collection<ItemHeader> getHeaders() {
		return headers;
	}
	
	/**
	 * Creates a new instance of packet holding collection of message headers.
	 * 
	 * @param messageHeaders Collection of message headers received by the current node.
	 */
	public MessageHeader(Collection<ItemHeader> messageHeaders) {
		this.headers = new ArrayList<ItemHeader>(messageHeaders);
	}
	
	@Override
	public String toString() {
		return headers.toString();
	}
}
