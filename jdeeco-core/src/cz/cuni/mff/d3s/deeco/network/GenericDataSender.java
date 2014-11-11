/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface GenericDataSender<TData> {
	void sendData(TData data, String recipient);
	void broadcastData(TData data);
}
