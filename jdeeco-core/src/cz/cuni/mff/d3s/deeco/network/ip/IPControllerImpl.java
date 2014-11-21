/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.DataSender;

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
		IPTable ipTable = getIPTable(data.getPartition());
		
		ipTable.addAll(data.getAddresses());
	}
	
	public IPControllerImpl() {
		this.ipTables = new HashMap<Object, IPTable>();
		this.receiver = new IPDataReceiverHandler(this);
	}
}
