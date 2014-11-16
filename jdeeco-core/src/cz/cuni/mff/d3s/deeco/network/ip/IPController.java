/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

/**
 * Manages and provides collection of IP addresses known by current host.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface IPController {
	public IPTable getIPTable(/* may be there should be some key here */);
	
	/**
	 * Gets {@link IPDataReceiver} used by current {@link IPController} to receive
	 * notifications about IP address changes from the network.
	 * 
	 * @return Instance of {@link IPDataReceiver}
	 */
	public IPDataReceiver getIPDataReceiver();
}
