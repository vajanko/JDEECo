/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cz.cuni.mff.d3s.deeco.network.DataReceiver;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPControllerImpl implements IPController, IPDataReceiver {

	private DataReceiver receiver;
	// key: partitionValue, value: partition IP table
	private Map<Object, IPTable> ipTables;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPController#getIPTable(java.lang.String)
	 */
	@Override
	public IPTable getIPTable(Object partitionValue) {
		
		if (!ipTables.containsKey(partitionValue))
			ipTables.put(partitionValue, new IPTable());
		
		return ipTables.get(partitionValue);
	}
	
	/**
	 * Gets {@link DataReceiver} used by current {@link IPController} to receive
	 * notifications about IP address changes from the network.
	 * 
	 * @return Instance of {@link DataReceiver}
	 */
	public DataReceiver getDataReceiver() {
		return receiver;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver#receive(java.util.List)
	 */
	@Override
	public void receive(IPData data) {
		IPTable ipTable = getIPTable(data.getPartitionValue());
		
		ipTable.clear();
		ipTable.addAll(data.getAddresses());
		System.out.println(this.toString());
	}
	
	public IPControllerImpl() {
		this.ipTables = new HashMap<Object, IPTable>();
		this.receiver = new IPDataReceiverHandler(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Entry<Object, IPTable> item : ipTables.entrySet()) {
			str.append(item.getKey().toString() + "->" + item.getValue().toString() + "\n");
		}
		return str.toString();
	}
}
