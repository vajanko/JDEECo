/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

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
	private Set<String> partitions;
	private Set<Object> range;
	private Map<Object, KnowledgeData> data;
	
	public DataReceiver getDataReceiver() { 
		return receiver;
	}

	public Map<Object, KnowledgeData> getKnowledge() {
		return data;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.GenericDataReceiver#receive(java.lang.Object)
	 */
	@Override
	public void receive(List<? extends KnowledgeData> obj) {
		for (KnowledgeData kd : obj) {
			for (String part : partitions) {
				Object val = KnowledgeHelper.getValue(kd, part);
				if (val != null && range.contains(val)) {
					data.put(val, kd);
					/*if (!data.containsKey(val)) {
						data.put(val, kd);
					}
					else if (data.get(val).getMetaData().versionId < kd.getMetaData().versionId) {
						data.put(val, kd);
					}*/
				}
			}
		}
		
	}
	
	/**
	 * 
	 */
	public KnowledgeProvider(Set<String> partitions, Set<Object> range) {
		this.partitions = partitions;
		this.range = range;
		this.receiver = new KnowledgeDataReceiverHandler(this);
		this.data = new HashMap<Object, KnowledgeData>();
	}
}
