package cz.cuni.mff.d3s.deeco.connectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.knowledge.ReadOnlyKnowledgeManager;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

@Component
public class Connector extends EnsembleComponent {
	
	// Registry of well known relay nodes
	public HashMap<String, ConnectorRegistry> registry;
	
	// IP address or host name of node where current Connector is deployed
	//public String id;
	// Knowledge data of requesting nodes are transformed by a hash function
	public String key;
	
	// Gets a list of available relay nodes with respect to given sender and it's
	// knowledge data.
	public Collection<String> getAvailibleRelays(KnowledgeData data, ReadOnlyKnowledgeManager sender) {
		String key = getDataKey(data, sender);
		ConnectorRegistry reg = getRegistry(key);
		
		return reg.getRelays();
	}
	
	private ConnectorRegistry getRegistry(String key) {
		if (registry.containsKey(key)) {
			return registry.get(key);
		}
		else {
			// TODO: find out where is the value for this key stored
			// key-space must be divided
			return new ConnectorRegistry(key);
		}
	}
	
	// Evaluate boundary condition
	private static String getDataKey(KnowledgeData data, ReadOnlyKnowledgeManager sender) {
		return "key";
	}
	
	public Connector(String id, String key) {
		super(id);
		this.key = key;
		registry = new HashMap<String, ConnectorRegistry>();
		
		ConnectorRegistry local = new ConnectorRegistry(key);
		local.add(id);
		registry.put(key, local);
	}

	@Process
	//@PeriodicScheduling(period = 2000)
	public static void process(
			@In("id") String id,
			@In("key") String key,
			@TriggerOnChange @In("registry") HashMap<String, ConnectorRegistry> registry) {
		// There is nothing to be done here
		// Connectors only communicate on the ensemble level
		System.out.print("Node " + id + " registry [ ");
		for (ConnectorRegistry reg : registry.values()) {
			System.out.print(reg.getKey() + ": (");
			int count = reg.getRelays().size();
			int i = 0;				
			for (String rel : reg.getRelays()) {
				System.out.print(rel);
				if (i++ < count - 1)
					System.out.print(", ");
			}
			System.out.print(") ");
		}
		System.out.println("]");
	}
}

class ConnectorRegistry {
	
	private String key;
	
	public String getKey() {
		return key;
	}
	
	private List<String> relays;
	
	public void add(String relay) {
		relays.add(relay);
	}
	public void remove(String relay) {
		relays.remove(relay);
	}
	public Collection<String> getRelays() {
		return relays;
	}
	
	public ConnectorRegistry(String key) {
		this.key = key; 
		relays = new ArrayList<String>();
	}
}
