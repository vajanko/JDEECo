package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashedIPGossipStorage implements IPGossipStorage {
	Map<Object, Set<String>> database = new HashMap<Object, Set<String>>();
	
	public synchronized Set<String> getAndUpdate(Object key, String address) {
		Set<String> dests = database.get(key);
		if(dests == null) {
			dests = new HashSet<String>();
			database.put(key, dests);
		}
		dests.add(address);
		return dests;
	}
}
