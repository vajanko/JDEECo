/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeHelper;
import cz.cuni.mff.d3s.deeco.network.ip.IPController;
import cz.cuni.mff.d3s.deeco.network.ip.IPRegister;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPGossipConnectorStrategy implements IPGossipStrategy {
	private IPController controller;
	private Collection<String> partitions;

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

	public IPGossipConnectorStrategy(Set<String> partitions, IPController controller) {
		this.partitions = new ArrayList<String>(partitions);
		this.controller = controller;
	}
}
