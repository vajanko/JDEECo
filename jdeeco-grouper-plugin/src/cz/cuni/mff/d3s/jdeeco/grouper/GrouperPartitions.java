/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates component partitioning known by a grouper component.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class GrouperPartitions {
	private Set<KnowledgePartition> partitions;
	
	public Collection<KnowledgePartition> getPartitions() {
		return partitions;
	}
	public void addPartition(KnowledgePartition partition) {
		this.partitions.add(partition);
	}
	
	public GrouperPartitions() {
		this.partitions = new HashSet<KnowledgePartition>();
	}
}
