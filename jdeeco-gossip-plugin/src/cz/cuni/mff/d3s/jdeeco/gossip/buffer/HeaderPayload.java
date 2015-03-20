/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.buffer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class HeaderPayload implements Serializable {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 1231352417793958761L;

	private Collection<ItemHeader> headers;
	
	public Collection<ItemHeader> getHeaders() {
		return headers;
	}
	
	public HeaderPayload(Collection<ItemHeader> messageHeaders) {
		this.headers = new ArrayList<ItemHeader>(messageHeaders);
	}
	
	@Override
	public String toString() {
		return headers.toString();
	}
}
