package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Collection;
import java.util.HashMap;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

public class DummyGossipStrategy implements IPGossipStrategy {
	static {
		globalRegistry = new HashMap<Object, ConnectorRegistry>();
		
		// TODO: initialise default registry values
	}
	private static HashMap<Object, ConnectorRegistry> globalRegistry;
	
	@Override
	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		Object key = "x";
		
		if (!globalRegistry.containsKey(key))
			globalRegistry.put(key, new ConnectorRegistry(key));
		
		ConnectorRegistry reg = globalRegistry.get(key);
		
		// TODO: update the registry with current knowledge data key
		reg.addRelay(sender.getId());
		
		//return new ConnectorRegistry(null).getRelays();
		return reg.getRelays();
	}
}
