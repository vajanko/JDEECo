/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Collection;
import java.util.HashSet;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;

/**
 * The core of gossip protocol for infrastructure network. Selects random recipients
 * from range of IP addresses.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class RangeRecipientSelector implements RecipientSelector {

	private int local;
	private int from;
	private int to;
	private int count;
	
	
	public RangeRecipientSelector(int local, int from, int to, int count) {
		this.local = local;
		this.from = from;
		this.to = to;
		this.count = count;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData)
	 */
	@Override
	public Collection<Address> getRecipients(KnowledgeData data) {
		Collection<Address> res = new HashSet<Address>();		
		for (int i = 0; i < count; i++) {
			int ip = GossipHelper.generator.nextInt(to - from) + from;
			if (ip == local)
				continue;
			res.add(AddressHelper.createIP(ip));
		}

		return res;
	}

}
