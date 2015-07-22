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
	public Object getPartitionByValue(KnowledgeData knowledgeData);
}
