/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a collection of message headers received by particular node.
 * Each header is associated with a time instance when message was received.
 * These data are used by nodes to discover that some specific message is 
 * missing on the current node.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MessageHeaders implements Serializable {
	
	private static final long serialVersionUID = -3056777165365098083L;
	
	private Map<String, Long> headers = new HashMap<String, Long>();
	
	public Map<String, Long> getHeaders() {
		return headers;
	}
}
