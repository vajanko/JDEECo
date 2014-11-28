package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
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
public class IPGossipRandomStrategy extends IPGossipPartitionStrategy {
	
	private Random gen = new Random();
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.IPGossipStrategy#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData, cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager)
	 */
	@Override
	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		
		ArrayList<String> ips = new ArrayList<String>();
		
		for (String part : partitions) {
			// value of partitionBy field
			Object val = KnowledgeHelper.getValue(data, part);
			if (val != null) {
				// example: get IP's of an ensemble partitioned by destination for "Berlin" group
				IPRegister table = controller.getRegister(val);
				ips.addAll(table.getAddresses());
			}
		}
		
		ips.remove(sender.getId());
		
		ArrayList<String> res = new ArrayList<String>();
		
		for (int i = 0; i < 2; i++) {
			int index = gen.nextInt(ips.size());
			while (res.contains(ips.get(index)))
				index++;
			res.add(ips.get(index));
		}
		
		return res;
	}
	
	public IPGossipRandomStrategy(Set<String> partitions, IPController controller) {
		super(partitions, controller);
	}
}
