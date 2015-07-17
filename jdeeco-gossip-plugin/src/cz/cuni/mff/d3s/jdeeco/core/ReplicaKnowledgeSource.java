/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

/**
 * Provides replica knowledge data.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public interface ReplicaKnowledgeSource {
	/**
	 * @return Copy of replica knowledge data prepared for network transfer.
	 */
	public Collection<KnowledgeData> getReplicaKnowledgeData();
}
