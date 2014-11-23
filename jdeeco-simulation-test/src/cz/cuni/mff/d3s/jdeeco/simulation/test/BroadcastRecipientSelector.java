package cz.cuni.mff.d3s.jdeeco.simulation.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.ReadOnlyKnowledgeManager;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.DirectRecipientSelector;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataPublisher;

public final class BroadcastRecipientSelector implements DirectRecipientSelector {

	private Collection<NodeComponent> components;
	
	public BroadcastRecipientSelector() {
		components = new ArrayList<NodeComponent>();
	}
	
	public void addComponet(NodeComponent c) {
		components.add(c);
	}
	
	public Collection<String> getRecipients(KnowledgeData data, ReadOnlyKnowledgeManager sender) {
		
		//data.getKnowledge().getValue();
		
		List<String> recipients = new ArrayList<String>();
		for (NodeComponent c : components) {
			recipients.add(c.id);
		}
		
		return recipients;
	}

}
