/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

/**
 * Component which is able to provide a private copy of local and replica knowledge data.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public interface KnowledgeProvider {
	/**
	 * @return Copy of local knowledge data prepared for network transfer.
	 */
	public Collection<KnowledgeData> getLocalKnowledgeData();
	/**
	 * @return Copy of replica knowledge data prepared for network transfer.
	 */
	public Collection<KnowledgeData> getReplicaKnowledgeData();
}
