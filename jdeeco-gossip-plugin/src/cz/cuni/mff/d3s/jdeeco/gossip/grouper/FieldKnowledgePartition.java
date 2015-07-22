/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;

/**
 * Represents knowledge partitioning by knowledge field value.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
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
	
	/**
	 * Creates a new instance of knowledge partitioning function using single
	 * knowledge field.
	 *  
	 * @param fieldName Full path to the knowledge field.
	 */
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
