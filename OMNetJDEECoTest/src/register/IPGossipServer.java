package register;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class IPGossipServer {
	
	private HashedIPGossipStorage storage;
	
	private Collection<String> getRecipients(RequestMessage msg) {
		
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
		
	}
	
	public IPGossipServer(HashedIPGossipStorage storage) {
		this.storage = storage;
	}
}
