/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class KnowledgeDataReceiverHandler extends GenericDataReceiverHandler<List<? extends KnowledgeData>> {

	@Override
	public void receiveData(Object data) {
		if (data == null || data instanceof List<?>) {
			dataReceiver.receive((List<? extends KnowledgeData>)data);
		}
	}
	
	/**
	 * @param dataReceiver
	 * @param dataType
	 */
	public KnowledgeDataReceiverHandler(GenericDataReceiver<List<? extends KnowledgeData>> dataReceiver) {
		super(dataReceiver, null);
	}

}
