package cz.cuni.mff.d3s.deeco.connectors;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.network.DirectGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

public final class SimpleGossipStrategy implements IPGossipStrategy {

	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		
		ArrayList<String> res = new ArrayList<String>();
		
		// there is only one well known node in the network
		//if (sender.getId() != "C1")
		//	res.add("C1");
		
		//data.getKnowledge().getValue(null);
		
		if (sender.getId().startsWith("R1")) {
			res.add("V1");
			res.add("V2");
			res.add("V3");
			res.add("V4");
		}
		else {
			res.add("R1");
		}
		
		return res;
	}

}
