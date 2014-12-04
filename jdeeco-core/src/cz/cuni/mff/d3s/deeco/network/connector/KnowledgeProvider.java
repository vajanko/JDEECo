/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class KnowledgeProvider extends DataReceiver<List<? extends KnowledgeData>> implements KnowledgeDataStore {
	
	// component id - knowledge data
	private Map<String, KnowledgeData> data;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.connector.KnowledgeDataStore#getAllKnowledgeData()
	 */
	@Override
	public Collection<KnowledgeData> getAllKnowledgeData() {
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
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public KnowledgeProvider() {
		super((Class<List<? extends KnowledgeData>>)(Class<?>)List.class);
		 
		this.data = new HashMap<String, KnowledgeData>();
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.DataReceiver#receive(java.lang.Object, double)
	 */
	@Override
	protected void receive(List<? extends KnowledgeData> data, double rssi) {
		for (KnowledgeData kd : data) {
			this.data.put(kd.getMetaData().componentId, kd);
		}
	}
	
}
