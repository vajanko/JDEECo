/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface GenericDataReceiver<TData> {
	
	// this method actually handles already deserialised object
	public void receive(TData obj);
	

	/**
	 * Gets {@link DataReceiver} implementation used to receive serialised objects from
	 * the network. {@link DataReceiver} receives deserialised object from the {@link PacketReceiver}
	 * and it should be passed to {@link receive} method if it is of type {@link TData}. 
	 * 
	 * @return Instance of {@link DataReceiver}
	 */
	// TODO: there should be method like this
	// but notice that an object capable of processing TData must not be able of receiving it
	// from packet receiver
	//public DataReceiver getDataReceiver();
}
