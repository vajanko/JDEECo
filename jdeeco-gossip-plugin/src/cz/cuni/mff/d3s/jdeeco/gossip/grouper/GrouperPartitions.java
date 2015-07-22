/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Encapsulates component partitioning known by a grouper component.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperPartitions {
	//private Set<KnowledgePartition> partitions;
	private Map<KnowledgePartition, GrouperRange> partitions;
	
	public Set<Entry<KnowledgePartition, GrouperRange>> getPartitions() {
		return partitions.entrySet();
	}
	public void addPartition(KnowledgePartition partition, GrouperRange range) {
		this.partitions.put(partition, range);
	}
	
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
