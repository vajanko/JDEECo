/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

/**
 * Allows to send directly or broadcast to the network generic type of
 * serializable object.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface GenericDataSender<TData> {
	/**
	 * Send given serializable object directly to provided recipient.
	 * 
	 * @param data Instance of serializable object to be sent.
	 * @param recipient Address of the remote host.
	 */
	void sendData(TData data, String recipient);
	/**
	 * Broadcast given serializable object to the network.
	 * @param data Instance of serializable object to be broadcasted.
	 */
	void broadcastData(TData data);
}
