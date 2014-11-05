package cz.cuni.mff.d3s.deeco.connectors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import cz.cuni.mff.d3s.deeco.knowledge.ReadOnlyKnowledgeManager;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.model.runtime.api.PathNode;
import cz.cuni.mff.d3s.deeco.network.DirectRecipientSelector;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.task.KnowledgePathHelper;

public final class BoundaryRecipientSelector implements DirectRecipientSelector {
	
	private List<String> recipients;
	
	public BoundaryRecipientSelector() {
		recipients = new ArrayList<String>();
	}
	
	public void addRecipient(String recipient) {
		this.recipients.add(recipient);
	}
	
	public Collection<String> getRecipients(KnowledgeData data, ReadOnlyKnowledgeManager sender) {
		
		// TODO: select list of recipients for current knowledge data and a sender
		
		//return recipients;
		
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
