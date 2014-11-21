package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.AbstractHost;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.DataSender;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeHelper;

/**
 * Provides list of recipient IP addresses for given knowledge data and sender.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPGossipClientStrategy implements IPGossipStrategy {
	
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
				IPTable table = controller.getIPTable(val);
				res.addAll(table.getAddresses());
			}
		}
		
		res.remove(sender.getId());
		return res;
	}
	
	public IPGossipClientStrategy(Set<String> partitions, IPController controller) {
		this.partitions = new ArrayList<String>(partitions);
		this.controller = controller;
	}
}
