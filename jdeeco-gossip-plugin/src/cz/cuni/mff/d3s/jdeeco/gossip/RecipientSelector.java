/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;

/**
 * For given packet provides a list of addresses where this packet should be sent.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public interface RecipientSelector {
	/**
	 * Gets collection of recipients of given knowledge.
	 * 
	 * @param data {@link KnowledgeData} to be sent over network.
	 * @return Collection of recipient addresses.
	 */
	public Collection<Address> getRecipients(KnowledgeData data);
}
