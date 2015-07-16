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
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MessageHeader implements Serializable {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 1231352417793958761L;

	private Collection<ItemHeader> headers;
	
	public Collection<ItemHeader> getHeaders() {
		return headers;
	}
	
	public MessageHeader(Collection<ItemHeader> messageHeaders) {
		this.headers = new ArrayList<ItemHeader>(messageHeaders);
	}
	
	@Override
	public String toString() {
		return headers.toString();
	}
}
