/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Collection;

import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;

/**
 * For given packet provides a list of addresses where this packet should be sent.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public interface RecipientSelector {
	/**
	 * Gets collection of recipients of given packet.
	 * 
	 * @param packet L2 packet to be sent.
	 * @return Collection of recipient addresses.
	 */
	public Collection<Address> getRecipients(L2Packet packet); 
}
