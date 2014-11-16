/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GenericDataReceiverHandler<TData> implements DataReceiver {
	
	private Class<TData> dataType;
	private GenericDataReceiver<TData> dataReceiver;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.DataReceiver#receiveData(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void receiveData(Object data) {
		if (data == null || data.getClass().isAssignableFrom(dataType)) {
			dataReceiver.receive((TData)data);
		}
	}
	
	public GenericDataReceiverHandler(GenericDataReceiver<TData> dataReceiver, Class<TData> dataType) {
		this.dataReceiver = dataReceiver;
		this.dataType = dataType;		
	}
}
