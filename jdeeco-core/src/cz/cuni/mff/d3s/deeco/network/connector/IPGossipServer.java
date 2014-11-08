package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.network.AbstractHost;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.DataSender;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.connector.AddressEntry.OperationType;
import cz.cuni.mff.d3s.deeco.task.KnowledgePathHelperTest;

public class IPGossipServer implements DataReceiver {
	
	private IPGossipStorage storage;
	private DataSender dataSender;
	private Set<String> partitions;
	
	/*private Collection<String> getRecipients(RequestMessage msg) {
		
		Collection<String> peers = new HashSet<String>();
		
		for (Object key : msg.getKeys()) {
			// Update record in database
			// TODO: getAndUpdate can be an interface, implement distributed gossip storage
			Set<String> dests = storage.getAndUpdate(key, msg.getSenderId());
			
			// Add destinations to peers
			peers.addAll(dests);
		}
		
		// Return (peers omitting sender)
		while (peers.remove(msg.getSenderId()))
			;
		
		return peers;
	}
	public void receiveMessage(RequestMessage msg) {
		
	}*/
	private ConnectorMessage createResponse(KnowledgeData knowledgeData, String sender) {
		ConnectorMessage msg = new ConnectorMessage();
			
		ValueSet knowledge = knowledgeData.getKnowledge();
		
		HashSet<String> peers = new HashSet<String>();
		for (KnowledgePath path : knowledge.getKnowledgePaths()) {
			if (partitions.contains(path.toString())) {
				Object key = knowledge.getValue(path);
				Set<String> addresses = this.storage.getAndUpdate(key, sender);
				
				peers.addAll(addresses);
			}
		}
		
		for (String peer : peers) {
			msg.getEntries().add(new AddressEntry(peer, OperationType.Add));
		}
		
		return msg;
	}
	
	public void receiveData(Object data) {
		if (!(data instanceof List<?>))
			return;
		
		// following code is for demonstration purposes only, it is going to be implemented in
		// totally different way
		List<KnowledgeData> knowledgeDataItems = (List<KnowledgeData>)data;
		
		for (KnowledgeData knowledgeData : knowledgeDataItems) {
			String sender = knowledgeData.getMetaData().sender;
			
			ConnectorMessage msg = createResponse(knowledgeData, sender);
			if (msg != null)
				dataSender.sendData(msg, sender);
		}
	};
	
	public IPGossipServer(DataSender dataSender, IPGossipStorage storage, RuntimeMetadata model) {
		this.dataSender = dataSender;
		this.storage = storage;
		this.partitions = new HashSet<String>();
		
		for (EnsembleDefinition ens : model.getEnsembleDefinitions()) {
			this.partitions.add(ens.getPartitionedBy());
		}
	}
}
