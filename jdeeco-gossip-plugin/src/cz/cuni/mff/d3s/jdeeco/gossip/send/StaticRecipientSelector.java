/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;

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
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData)
	 */
	@Override
	public Collection<Address> getRecipients(KnowledgeData data) {
		return this.register.getAddresses();
	}
	
}
