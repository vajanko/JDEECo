/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import cz.cuni.mff.d3s.deeco.network.ip.IPEntry.OperationType;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface IPNotifierService {
	void notify(String recipient, String address, OperationType op);
	void pushNotifications();
}