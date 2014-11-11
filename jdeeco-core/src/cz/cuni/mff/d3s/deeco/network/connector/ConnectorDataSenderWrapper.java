/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import cz.cuni.mff.d3s.deeco.network.DataSender;
import cz.cuni.mff.d3s.deeco.network.GenericDataSenderWrapper;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class ConnectorDataSenderWrapper extends GenericDataSenderWrapper<ConnectorMessage> implements ConnectorDataSender {

	public ConnectorDataSenderWrapper(DataSender sender) {
		super(sender);
	}

}
