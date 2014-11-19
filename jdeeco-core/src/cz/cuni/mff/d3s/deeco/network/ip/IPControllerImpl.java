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
	// key: partitionId, value: partition IP table
	private Map<String, IPTable> ipTables;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPController#getIPTable(java.lang.String)
	 */
	@Override
	public IPTable getIPTable(String partitionId) {
		
		if (!ipTables.containsKey(partitionId))
			ipTables.put(partitionId, new IPTable());
		
		return ipTables.get(partitionId);
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
//		for (IPEntry entry : data.getEntries()) {
//			switch(entry.getOperation()) {
//			case Add:
//				ipTable.addAddress(entry.getAddress());
//				break;
//			case Remove:
//				ipTable.removeAddress(entry.getAddress());
//				break;
//			default:
//				Log.w("Unknown operation type " + entry.getOperation() + " received from in IPData message");
//				break;
//			}
//		}
	}
	
	public IPControllerImpl(String partitionId, String initialHost) {
		this(partitionId, Arrays.asList(initialHost));
	}
	public IPControllerImpl(String partitionId, Collection<String> initialHosts) {
		this();
		
		IPTable table = getIPTable(partitionId);
		table.addAll(initialHosts);
	}
	public IPControllerImpl() {
		this.ipTables = new HashMap<String, IPTable>();
		this.receiver = new IPDataReceiverHandler(this);
	}
}
