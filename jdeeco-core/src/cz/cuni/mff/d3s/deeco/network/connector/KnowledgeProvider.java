/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiverHandler;
import cz.cuni.mff.d3s.deeco.network.KnowledgeHelper;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class KnowledgeProvider implements KnowledgeDataReceiver {
	
	private KnowledgeDataReceiverHandler receiver;
	// component id - knowledge data
	private Map<String, KnowledgeData> data;
	
	public DataReceiver getDataReceiver() { 
		return receiver;
	}

	public Collection<KnowledgeData> getKnowledge() {
		return data.values();
	}
	public void remove(KnowledgeData kd) {
		data.remove(kd.getMetaData().componentId);
	}
	public void removeAll(Collection<KnowledgeData> kds) {
		for (KnowledgeData kd : kds) {
			data.remove(kd);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.GenericDataReceiver#receive(java.lang.Object)
	 */
	@Override
	public void receive(List<? extends KnowledgeData> obj) {
		for (KnowledgeData kd : obj) {
			data.put(kd.getMetaData().componentId, kd);
		}
	}
	
	/**
	 * 
	 */
	public KnowledgeProvider() {
		this.receiver = new KnowledgeDataReceiverHandler(this);
		this.data = new HashMap<String, KnowledgeData>();
	}
}
