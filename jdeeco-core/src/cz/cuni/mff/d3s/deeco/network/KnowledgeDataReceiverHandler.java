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

	/**
	 * @param dataReceiver
	 * @param dataType
	 */
	public KnowledgeDataReceiverHandler(GenericDataReceiver<List<? extends KnowledgeData>> dataReceiver) {
		super(dataReceiver, new ArrayList<KnowledgeData>().getClass());
	}

}
