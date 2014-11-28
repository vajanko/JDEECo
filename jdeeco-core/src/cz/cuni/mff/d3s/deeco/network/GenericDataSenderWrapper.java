/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

/**
 * Wraps general {@link DataSender} which is able to send arbitrary serializable object
 * and allows to send only object of {@link TData} type.
 * In order to create sender for a particular type of data extends this class
 * and provide constructor with {@link DataSender} argument.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public abstract class GenericDataSenderWrapper<TData> implements GenericDataSender<TData> {
	
	private DataSender sender;
	
	@Override
	public void sendData(TData data, String recipient) {
		sender.sendData(data, recipient);
	}
	@Override
	public void broadcastData(TData data) {
		sender.broadcastData(data);
	}
	
	public GenericDataSenderWrapper(DataSender sender) {
		this.sender = sender;
	}
}
