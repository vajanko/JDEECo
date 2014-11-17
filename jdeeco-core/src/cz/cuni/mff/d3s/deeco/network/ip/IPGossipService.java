/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.DicEntry;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiverHandler;
import cz.cuni.mff.d3s.deeco.network.connector.ConnectorComponent;
import cz.cuni.mff.d3s.deeco.network.ip.IPEntry.OperationType;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPGossipService implements KnowledgeDataReceiver {
	
	private DataReceiver receiver;
	//private ConnectorComponent connector;
	private Set<String> partitions;
	
	private List<KnowledgeData> knowledgeQueue;
	
	public DataReceiver getDataReceiver() {
		return receiver;
	}
	
	public boolean empty() {
		return knowledgeQueue.size() == 0;
	}
	public KnowledgeData pop() {
		int index = knowledgeQueue.size() - 1;
		KnowledgeData kn = knowledgeQueue.get(index);
		knowledgeQueue.remove(index);
		return kn;
	}
	
	public Set<String> getPartitions() {
		return partitions;
	}
	
	/*private void processKnowledge(KnowledgeData knowledgeData, String sender) {
		
		ValueSet knowledge = knowledgeData.getKnowledge();
		
		for (KnowledgePath path : knowledge.getKnowledgePaths()) {
			
			if (partitions.contains(path.toString())) {
				Object key = knowledge.getValue(path);
				if (connector.range.contains(key)) {
					// current connector is responsible for this key
					connector.storage.getAndUpdate(key, sender);
				}
				else {
					// there is another connector responsible for this key
					connector.outputEntries.add(new DicEntry(key, sender));
				}
			}
		}
	}*/

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver#receive(java.util.List)
	 */
	@Override
	public void receive(List<? extends KnowledgeData> knowledgeData) {
//		for (KnowledgeData kd : knowledgeData) {
//			String sender = kd.getMetaData().sender;
//			processKnowledge(kd, sender);
//		}
		knowledgeQueue.addAll(knowledgeData);
	}
	
	/**
	 * 
	 */
	public IPGossipService(/*ConnectorComponent connector, Set<String> partitions*/) {
		this.receiver = new KnowledgeDataReceiverHandler(this);
		//this.connector = connector;
		//this.partitions = partitions;
		
		this.knowledgeQueue = new ArrayList<KnowledgeData>();
		this.partitions = new HashSet<String>();
	}

}
