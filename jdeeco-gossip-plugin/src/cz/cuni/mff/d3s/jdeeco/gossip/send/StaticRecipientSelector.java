/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Arrays;
import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
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
		// FIXME: this is a hack - do not publish grouper client component knowledge
		if (data.getMetaData().componentId.startsWith("GC"))
			return Arrays.asList();
		return this.register.getAddresses();
	}
	
	public void addRecipient(Address address) {
		this.register.add(address);
	}
}
