/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.Arrays;
import java.util.Collection;

import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPControllerImpl implements IPController, IPDataReceiver {

	private IPTable ipTable;
	private IPDataReceiverHandler ipReceiver;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPController#getIPTable()
	 */
	@Override
	public IPTable getIPTable() {
		return ipTable;
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPController#getIPDataReceiver()
	 */
	@Override
	public DataReceiver getDataReceiver() {
		return ipReceiver;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver#receive(java.util.List)
	 */
	@Override
	public void receive(IPData data) {
		for (IPEntry entry : data.getEntries()) {
			switch(entry.getOperation()) {
			case Add:
				ipTable.addAddress(entry.getAddress());
				break;
			case Remove:
				ipTable.removeAddress(entry.getAddress());
				break;
			default:
				Log.w("Unknown operation type " + entry.getOperation() + " received from in IPData message");
				break;
			}
		}
	}

	public IPControllerImpl(String initialHost) {
		this(Arrays.asList(initialHost));
	}
	public IPControllerImpl(Collection<String> initialHosts) {
		this.ipTable = new IPTable(initialHosts);
		this.ipReceiver = new IPDataReceiverHandler(this);
	}
}
