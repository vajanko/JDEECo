/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Instance of this object is sent when a node requests particular message(s)
 * to be rebroadcasted.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PullKnowledgePayload implements Serializable {
	
	private static final long serialVersionUID = 1736658246438509897L;
	
	private Collection<String> messages;
	
	/**
	 * @return Collection of message IDs to be broadcasted again.
	 */
	public Collection<String> getMessages() {
		return messages;
	}
	
	public PullKnowledgePayload(Collection<String> messages) {
		this.messages = new ArrayList<String>(messages);
	}
}
