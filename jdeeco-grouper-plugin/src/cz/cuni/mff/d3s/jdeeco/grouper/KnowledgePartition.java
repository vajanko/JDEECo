/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;

/**
 * Represents particular knowledge partitioning. Current implementation
 * only reads a field value from knowledge, but this behavior can be extended
 * in the future.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class KnowledgePartition {
	private String fieldName;
	
	public Object getPartitionByValue(KnowledgeData knowledgeData) {
		KnowledgePath path = KnowledgeDataHelper.getPath(knowledgeData, fieldName);
		return knowledgeData.getKnowledge().getValue(path);
	}
	
	public KnowledgePartition(String fieldName) {
		this.fieldName = fieldName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("field: %s", fieldName);
	}
}
