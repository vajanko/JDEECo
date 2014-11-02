package cz.cuni.mff.d3s.deeco.connectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.ReadOnlyKnowledgeManager;
import cz.cuni.mff.d3s.deeco.network.DirectRecipientSelector;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

public final class BoundaryRecipientSelector implements DirectRecipientSelector {
	
	public BoundaryRecipientSelector() {
		// TODO: add parameters
	}
	
	public Collection<String> getRecipients(KnowledgeData data, ReadOnlyKnowledgeManager sender) {
		
		List<String> recipients = new ArrayList<String>();
		
		// TODO: select list of recipients for current knowledge data and a sender
		if (sender.getId() == "V1") {
			recipients.add("V2");
			recipients.add("V3");
		}
		else if (sender.getId() == "V2") {
			recipients.add("V1");
			recipients.add("V3");
		}
		else if (sender.getId() == "V3") {
			recipients.add("V1");
			recipients.add("V2");
		}
		
		return recipients;
	}

}
