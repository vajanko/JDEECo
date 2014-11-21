/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiverHandler;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class KnowledgeQueue implements KnowledgeDataReceiver {
	
	private DataReceiver receiver;
	private Set<String> partitions;
	
	private LinkedList<KnowledgeData> knowledgeQueue;
	
	public DataReceiver getDataReceiver() {
		return receiver;
	}
	
	public boolean empty() {
		return knowledgeQueue.size() == 0;
	}
	public KnowledgeData pop() {
		return knowledgeQueue.removeFirst();
	}
	
	public Set<String> getPartitions() {
		return partitions;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver#receive(java.util.List)
	 */
	@Override
	public void receive(List<? extends KnowledgeData> knowledgeData) {
		knowledgeQueue.addAll(knowledgeData);
	}
	
	public KnowledgeQueue() {
		this.receiver = new KnowledgeDataReceiverHandler(this);	
		this.knowledgeQueue = new LinkedList<KnowledgeData>();
		this.partitions = new HashSet<String>();
	}

}
