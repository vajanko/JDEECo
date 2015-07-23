/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperPartitions;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRange;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRole;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.KnowledgePartition;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * Provides a set of recipients for particular knowledge before publish on a grouper node.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class ServerRecipientSelector implements RecipientSelector {
	
	/**
	 * Configuration property expressing how many of the known hosts set
	 * will be selected for publish.
	 */
	public static final String PUBLISH_COUNT = "deeco.sendKN.count";
	
	/**
	 * IP address of the current node.
	 */
	private IPAddress address;
	/**
	 * Grouper partitions
	 */
	private GrouperPartitions partitions;
	/**
	 * Recipient selector as defined on the non-gropuer nodes.
	 */
	private RecipientSelector innerSelector;
	/**
	 * How many random recipients will be returned from the recipient selector.
	 */
	private int count = 2;	// from config
	
	/**
	 * Creates a new instance of recipient selector deployed on the grouper node.
	 * 
	 * @param address IP address of the current node.
	 * @param partitions Grouper partitions
	 * @param innerSelector Recipient selector as deplyed on the non-grouper nodes.
	 */
	public ServerRecipientSelector(IPAddress address, GrouperPartitions partitions, RecipientSelector innerSelector) {
		this.address = address;
		this.partitions = partitions;
		this.innerSelector = innerSelector;
		this.count = Integer.getInteger(PUBLISH_COUNT, 0);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData)
	 */
	@Override
	public Collection<Address> getRecipients(KnowledgeData data) {
		Collection<Address> all = getAllRecipients(data);
		if (all.size() <= count)
			return all;
		
		Address []allArray = new Address[all.size()];
		all.toArray(allArray);
		
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
				// get recipients from grouper knowledge
				return getGrouperKnowledgeRecipients(data);
			} else {
				// get recipients from the register
				return innerSelector.getRecipients(data);
			}
		} else {
			// REBROADCAST
			if (isGrouperKnowledge(data)) {
				// do not rebroadcast grouper knowledge
			} else {
				// component knowledge is rebroadcasted when necessary
				return getPartitionedKnowledgeRecipinets(data);
			}
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
		
		return role != null && role == GrouperRole.server;
	}
	/**
	 * Gets all recipients of knowledge data which is partitioned by some of the current
	 * grouper partitions.
	 * 
	 * @param data Published knowledge data.
	 * @return Set of addresses where given knowledge should be published.
	 */
	private Collection<Address> getPartitionedKnowledgeRecipinets(KnowledgeData data) {
		// TODO: join all address register that match some partition
		for (Entry<KnowledgePartition, GrouperRange> item : partitions.getPartitions()) {
			KnowledgePartition part = item.getKey();
			GrouperRange range = item.getValue();
			
			Object partVal = part.getPartitionByValue(data);
			if (range.inRange(partVal)) {
				// this node grouper is responsible for given knowledge - do not rebroadcast
				return Arrays.asList();
			} else {
				// this node grouper is not responsible for given knowledge - rebroadcast
				// to other grouper
				//return innerSelector.getRecipients(data);
			}
		}
		
		return innerSelector.getRecipients(data);
	}
	
	@SuppressWarnings("unchecked")
	private Collection<Address> getGrouperKnowledgeRecipients(KnowledgeData data) {
		KnowledgePath groupPath = KnowledgeDataHelper.getPath(data, "groupMembers");
		Set<String> groupMembers = (Set<String>)data.getKnowledge().getValue(groupPath);
		
		HashSet<Address> res = new HashSet<Address>();
		for (String adr : groupMembers)
			res.add(new IPAddress(adr));
		
		return res;
	}

}
