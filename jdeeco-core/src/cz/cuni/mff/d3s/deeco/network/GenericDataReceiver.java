/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface GenericDataReceiver<TData> {
	public void receive(TData obj);
}
