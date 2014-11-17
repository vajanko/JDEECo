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
import cz.cuni.mff.d3s.deeco.network.ip.IPEntry.OperationType;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPControllerImpl implements IPController, IPDataReceiver {

	private IPTable ipTable;
	private DataReceiver receiver;
	private IPNotifierService notifierService;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPController#getIPTable()
	 */
	@Override
	public IPTable getIPTable() {
		return ipTable;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPController#notify(java.lang.String, java.lang.String, cz.cuni.mff.d3s.deeco.network.ip.IPEntry.OperationType)
	 */
	@Override
	public void notify(String recipient, String address, OperationType op) {
		notifierService.notify(recipient, address, op);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPNotifierService#pushNotifications()
	 */
	@Override
	public void pushNotifications() {
		notifierService.pushNotifications();
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPController#getIPDataReceiver()
	 */
	@Override
	public DataReceiver getDataReceiver() {
		return receiver;
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

	public IPControllerImpl(String initialHost, DataSender sender) {
		this(Arrays.asList(initialHost), sender);
	}
	public IPControllerImpl(Collection<String> initialHosts, DataSender sender) {
		this.ipTable = new IPTable(initialHosts);
		this.receiver = new IPDataReceiverHandler(this);
		this.notifierService = new IPNotifierServiceImpl(new IPDataSenderWrapper(sender));
	}
}
