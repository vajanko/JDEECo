/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRole;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * Provides a set of recipients for particular knowledge before publish.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class ClientRecipinetSelector implements RecipientSelector {
	
	/**
	 * Configuration property expressing how many of the known hosts set
	 * will be selected for publish.
	 */
	public static final String PUBLISH_COUNT = "deeco.sendKN.count";

	/**
	 * IP address of the current node.
	 */
	private IPAddress address;
	
	private RecipientSelector innerSelector;
	private int count = 2;
	
	public ClientRecipinetSelector(IPAddress address, RecipientSelector innerSelector) {
		this.address = address;
		this.innerSelector = innerSelector;
		this.count = Integer.getInteger(PUBLISH_COUNT, 0);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData)
	 */
	@Override
	public Collection<Address> getRecipients(KnowledgeData data) {
		
		// retrieves recipients from the inner source
		Collection<Address> all = getAllRecipients(data);
		if (all.size() <= count)
			return all;
		
		Address []allArray = new Address[all.size()];
		all.toArray(allArray);
		
		// filter random subset of particular size
		HashSet<Address> res = new HashSet<Address>(count);
		for (int i = 0; i < count; i++) {
			int idx = GossipHelper.generator.nextInt(all.size());
			res.add(allArray[idx]);
		}
		
		return res;
	}
	
	/**
	 * Gets the whole set of recipients for particular knowledge data. These recipients
	 * could be further filtered.
	 * 
	 * @param data Published knowledge data.
	 * @return Set of addresses where given knowledge should be published.
	 */
	private Collection<Address> getAllRecipients(KnowledgeData data) {
		Address senderAdr = AddressHelper.decodeAddress(data.getMetaData().componentId);
		if (senderAdr.equals(this.address)) {
			// PUBLISH
			if (isGrouperKnowledge(data)) {
				// grouper client component knowledge is not published at all
			} else {
				// get recipients from the register
				return innerSelector.getRecipients(data);
			}
		} else {
			// REBROADCAST
			// nodes without grouper do not rebroadcast at all
		}
		
		return Arrays.asList();
	}
	
	/**
	 * Gets value indicating whether given knowledge comes from the grouper.
	 * 
	 * @param data Knowledge data to be classified.
	 * @return True if given knowledge data comes from the grouper otherwise false.
	 */
	private boolean isGrouperKnowledge(KnowledgeData data) {
		KnowledgePath rolePath = KnowledgeDataHelper.getPath(data, "grouperRole");
		GrouperRole role = (GrouperRole)data.getKnowledge().getValue(rolePath);
		
		return role != null && role == GrouperRole.client;
	}
}
