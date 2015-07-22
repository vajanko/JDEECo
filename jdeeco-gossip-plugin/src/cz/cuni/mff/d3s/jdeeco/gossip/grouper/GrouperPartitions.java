/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Encapsulates component partitioning known by a grouper component. Components
 * can be partitioned by multiple functions.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperPartitions {
	
	/**
	 * Collection of grouper partitionings and associated range for which is the 
	 * current grouper responsible.
	 */
	private Map<KnowledgePartition, GrouperRange> partitions;
	
	/**
	 * Gets collection of grouper partitionings and associated range for which is the
	 * current grouper responsible.
	 * @return Collection of partitionings.
	 */
	public Set<Entry<KnowledgePartition, GrouperRange>> getPartitions() {
		return partitions.entrySet();
	}
	/**
	 * Addes new grouper partitioning with associated range of values.
	 * @param partition Grouper partitioning function.
	 * @param range Groper range of values.
	 */
	public void addPartition(KnowledgePartition partition, GrouperRange range) {
		this.partitions.put(partition, range);
	}
	
	/**
	 * Creates new collection of grouper partitionings.
	 */
	public GrouperPartitions() {
		this.partitions = new HashMap<KnowledgePartition, GrouperRange>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("partitions: %s", partitions);
	}
}
