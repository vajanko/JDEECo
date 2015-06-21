/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperPartitions;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRange;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRole;
import cz.cuni.mff.d3s.jdeeco.grouper.KnowledgePartition;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class ServerRecipientSelector implements RecipientSelector {
	
	public static final String PUBLISH_COUNT = "deeco.sendKN.count";
	
	private IPAddress address;
	private GrouperPartitions partitions;
	private RecipientSelector innerSelector;
	private int count = 2;	// from config
	
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
			int idx = GossipPlugin.generator.nextInt(all.size());
			res.add(allArray[idx]);
		}
		
		return res;
	}
	
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
	
	private boolean isGrouperKnowledge(KnowledgeData data) {
		KnowledgePath rolePath = KnowledgeDataHelper.getPath(data, "grouperRole");
		GrouperRole role = (GrouperRole)data.getKnowledge().getValue(rolePath);
		
		return role != null && role == GrouperRole.server;
	}
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
