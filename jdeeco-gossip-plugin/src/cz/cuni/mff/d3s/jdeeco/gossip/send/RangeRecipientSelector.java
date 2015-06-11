/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Collection;
import java.util.HashSet;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class RangeRecipientSelector implements RecipientSelector {

	private int from;
	private int to;
	
	/**
	 * 
	 */
	public RangeRecipientSelector(int from, int to) {
		this.from = from;
		this.to = to;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData)
	 */
	@Override
	public Collection<Address> getRecipients(KnowledgeData data) {
		Collection<Address> res = new HashSet<Address>();
		for (int i = 0; i < 3; i++) {
			int ip = GossipPlugin.generator.nextInt(to - from) + from;
			res.add(AddressHelper.createIP(ip));
		}

		return res;
	}

}
