/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cz.cuni.mff.d3s.deeco.network.ip.IPEntry.OperationType;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPNotifierServiceImpl implements IPNotifierService {
	
	private IPDataSender sender;
	private Map<String, IPData> messages;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPNotifierService#notify(java.lang.String, java.lang.String, cz.cuni.mff.d3s.deeco.network.ip.IPEntry.OperationType)
	 */
	@Override
	public void notify(String recipient, String address, OperationType op) {
		
		if (!messages.containsKey(recipient))
			messages.put(recipient, new IPData());
		
		IPData data = messages.get(recipient);
		data.addEntry(address, op);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPNotifierService#pushNotifications()
	 */
	@Override
	public void pushNotifications() {
		
		for (Entry<String, IPData> entry : messages.entrySet()) {
			sender.sendData(entry.getValue(), entry.getKey());
		}
		
		messages.clear();
	}
	
	public IPNotifierServiceImpl(IPDataSender sender) {
		this.sender = sender;
		this.messages = new HashMap<String, IPData>();
	}
	
}
