/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Collection;

import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class StaticRecipientSelector implements RecipientSelector {

	private AddressRegister register;
	
	public StaticRecipientSelector(AddressRegister register) {
		this.register = register;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.send.RecipientSelector#getRecipients(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public Collection<Address> getRecipients(L2Packet packet) {
		return this.register.getAddresses();
	}
	
}
