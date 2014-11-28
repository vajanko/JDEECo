/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import cz.cuni.mff.d3s.deeco.network.DataSender;
import cz.cuni.mff.d3s.deeco.network.GenericDataSenderWrapper;

/**
 * Wraps general {@link DataSender} and allows for sending only {@link IPData} type.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPDataSenderWrapper extends GenericDataSenderWrapper<IPData> implements IPDataSender {

	public IPDataSenderWrapper(DataSender sender) {
		super(sender);
	}

}
