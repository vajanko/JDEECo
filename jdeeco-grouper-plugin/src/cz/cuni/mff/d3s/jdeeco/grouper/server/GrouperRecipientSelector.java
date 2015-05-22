/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRole;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperRecipientSelector implements RecipientSelector {

	private RecipientSelector innerSelector;
	
	public GrouperRecipientSelector(RecipientSelector innerSelector) {
		this.innerSelector = innerSelector;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Address> getRecipients(KnowledgeData data) {
		// check whether this is gouper
		KnowledgePath rolePath = KnowledgeDataHelper.getPath(data, "role");
		GrouperRole role = (GrouperRole)data.getKnowledge().getValue(rolePath);
		if (role == null || role != GrouperRole.server) {
			return this.innerSelector.getRecipients(data);
		}
		
		// get group data
		KnowledgePath groupPath = KnowledgeDataHelper.getPath(data, "groupMembers");
		Set<String> groupMembers = (Set<String>)data.getKnowledge().getValue(groupPath);
		
		HashSet<Address> res = new HashSet<Address>();
		for (String adr : groupMembers)
			res.add(new IPAddress(adr));
		
		return res;
	}

}
