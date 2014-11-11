/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

import java.util.List;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class KnowledgeDataSenderWrapper extends GenericDataSenderWrapper<List<? extends KnowledgeData>> implements KnowledgeDataSender {

	public KnowledgeDataSenderWrapper(DataSender sender) {
		super(sender);
	}
}
