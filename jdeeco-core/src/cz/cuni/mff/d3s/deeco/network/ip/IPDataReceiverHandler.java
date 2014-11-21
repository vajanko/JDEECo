/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import cz.cuni.mff.d3s.deeco.network.GenericDataReceiverHandler;

/**
 * An implementation of {@link DataReceiver} which correctly receives only 
 * instances of {@link IPData}
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPDataReceiverHandler extends GenericDataReceiverHandler<IPData> {
	
	@Override
	public void receiveData(Object data) {
		if (data == null || data instanceof IPData) {
			dataReceiver.receive((IPData)data);
		}
	}
	
	public IPDataReceiverHandler(IPDataReceiver receiver) {
		super(receiver, IPData.class);
	}
}
