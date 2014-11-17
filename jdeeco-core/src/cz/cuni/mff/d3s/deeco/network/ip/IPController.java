/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.ip.IPEntry.OperationType;

/**
 * Manages and provides collection of IP addresses known by current host.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface IPController{
	
	public IPTable getIPTable();
	
	void notify(String recipient, String address, OperationType op);
	
	void pushNotifications();
	
	/**
	 * Gets {@link DataReceiver} used by current {@link IPController} to receive
	 * notifications about IP address changes from the network.
	 * 
	 * @return Instance of {@link DataReceiver}
	 */
	public DataReceiver getDataReceiver();
}
