/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

/**
 * Provides knowledge data for publishing on the network.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public interface KnowledgeSource {
	/**
	 * Gets collection of knowledge data which can be published on the network.
	 * 
	 * @return Collection of knowledge data.
	 */
	public Collection<KnowledgeData> getKnowledge();
}
