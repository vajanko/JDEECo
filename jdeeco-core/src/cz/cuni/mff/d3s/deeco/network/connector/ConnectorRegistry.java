package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ConnectorRegistry {
	
	private Object key;
	
	public Object getKey() {
		return key;
	}
	
	private List<String> relays;
	
	public void addRelay(String relay) {
		if (!relays.contains(relay))
			relays.add(relay);
	}
	public void removeRelay(String relay) {
		relays.remove(relay);
	}
	public Collection<String> getRelays() {
		return relays;
	}
	
	public String toString() {
		String res = key + " = {";
		for (int i = 0; i < relays.size() - 1; i++)
			res += relays.get(i) + ", ";
		if (relays.size() > 0)
			res += relays.get(relays.size() - 1);
		res += "}";
		return res;
	}
	
	public ConnectorRegistry(Object key) {
		this.key = key; 
		relays = new ArrayList<String>();
	}
}
