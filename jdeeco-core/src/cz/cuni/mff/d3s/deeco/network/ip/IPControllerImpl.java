/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPControllerImpl implements IPController, IPDataReceiver, DataReceiver {

	private IPTable ipTable;
	
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
	public IPDataReceiver getIPDataReceiver() {
		return this;
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.DataReceiver#receiveData(java.lang.Object)
	 */
	@Override
	public void receiveData(Object data) {
		if (data instanceof IPData)
			receive((IPData)data);
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

	public IPControllerImpl() {
		this.ipTable = new IPTable();
	}
}
