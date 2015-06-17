/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;

/**
 * Represents knowledge partitioning by knowledge field value.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class FieldKnowledgePartition implements KnowledgePartition  {
	
	private String fieldName;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.grouper.KnowledgePartition#getPartitionByValue(cz.cuni.mff.d3s.deeco.network.KnowledgeData)
	 */
	@Override
	public Object getPartitionByValue(KnowledgeData knowledgeData) {
		KnowledgePath path = KnowledgeDataHelper.getPath(knowledgeData, fieldName);
		return knowledgeData.getKnowledge().getValue(path);
	}
	
	public FieldKnowledgePartition(String fieldName) {
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
