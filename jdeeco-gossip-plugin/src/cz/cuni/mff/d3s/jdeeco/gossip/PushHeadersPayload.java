/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ItemHeader;

/**
 * Wraps a collection of message headers received by particular node.
 * Each header is associated with a time instance when message was received.
 * These data are used by nodes to discover that some specific message is 
 * missing on the current node.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PushHeadersPayload implements Serializable {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -3056777165365098083L;
	
	private Collection<ItemHeader> headers;
	
	public Collection<ItemHeader> getHeaders() {
		return headers;
	}
	
	public PushHeadersPayload(Collection<ItemHeader> messageHeaders) {
		this.headers = new ArrayList<ItemHeader>(messageHeaders);
	}
	
	@Override
	public String toString() {
		return headers.toString();
	}
}
