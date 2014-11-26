package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeHelper;

/**
 * Provides list of recipient IP addresses for given knowledge data and sender.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPGossipClientStrategy extends IPGossipBaseStrategy {
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.IPGossipStrategy#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData, cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager)
	 */
	@Override
	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		
		ArrayList<String> res = new ArrayList<String>();
		
		for (String part : partitions) {
			// value of partitionBy field
			Object val = KnowledgeHelper.getValue(data, part);
			if (val != null) {
				// example: get IP's of an ensemble partitioned by destination for "Berlin" group
				IPRegister table = controller.getRegister(val);
				res.addAll(table.getAddresses());
			}
		}
		
		res.remove(sender.getId());
		return res;
	}
	
	public IPGossipClientStrategy(Set<String> partitions, IPController controller) {
		super(partitions, controller);
	}
}
