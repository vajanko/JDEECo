package register;

import java.io.Serializable;
import java.util.Collection;

public class ResponseMessage implements Serializable {
	
	public String[] addPeers;
	public String[] removePeers;

	public ResponseMessage(Collection<String> addPeers, Collection<String> removePeers) {
		this.addPeers = addPeers.toArray(new String[addPeers.size()]);
		this.removePeers = removePeers.toArray(new String[removePeers.size()]);
	}
}
