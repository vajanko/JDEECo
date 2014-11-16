/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

/**
 * An object that is capable of processing {@link IPData} coming from the network.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface IPDataReceiver {
	public void receive(IPData data);
}
