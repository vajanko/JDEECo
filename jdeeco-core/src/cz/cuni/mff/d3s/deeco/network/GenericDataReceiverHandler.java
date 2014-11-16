/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
//		Class<?> c = dataReceiver.getClass();
//		Type t = c.getGenericInterfaces()[1];
//		
//		Class<?> tc = t.getClass();
//		t = tc.getGenericInterfaces()[0];
//		
//		ParameterizedType pt = (ParameterizedType)t;
//		Type[] ta = pt.getActualTypeArguments();
//		Type ta0 = ta[0];
//		Class<?> clazz = (Class<?>)ta0.getClass();
		//dataType = (Class<TData>)((ParameterizedType) dataReceiver.getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
	}
}
