/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GenericDataSenderWrapper<TData> implements GenericDataSender<TData> {
	
	private DataSender sender;
	
	@Override
	public void sendData(TData data, String recipient) {
		sender.sendData(data, recipient);
	}
	@Override
	public void broadcastData(TData data) {
		sender.broadcastData(data);
	}
	
	/**
	 * 
	 */
	public GenericDataSenderWrapper(DataSender sender) {
		this.sender = sender;
	}
}
