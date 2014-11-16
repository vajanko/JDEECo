/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import cz.cuni.mff.d3s.deeco.network.GenericDataReceiver;

/**
 * An object that is capable of processing {@link IPData} coming from the network.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface IPDataReceiver extends GenericDataReceiver<IPData> {
	//public void receive(IPData data);
}
