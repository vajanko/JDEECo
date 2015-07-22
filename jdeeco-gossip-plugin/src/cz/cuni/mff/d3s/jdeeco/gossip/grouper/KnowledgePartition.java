/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

/**
 * Represents particular knowledge partitioning.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public interface KnowledgePartition {
	/**
	 * From given knowledge data extract the partition key.
	 * 
	 * @param knowledgeData Knowledge data from which partition key should be extracted.
	 * @return Partition key of particular knowledge.
	 */
	public Object getPartitionByValue(KnowledgeData knowledgeData);
}
