/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeSource;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class BasicKnowledgeSource implements KnowledgeSource {

	private KnowledgeProviderPlugin knowledgeProvider;

	public BasicKnowledgeSource(KnowledgeProviderPlugin knowledgeProvider) {
		this.knowledgeProvider = knowledgeProvider;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeSource#getKnowledge()
	 */
	@Override
	public Collection<KnowledgeData> getKnowledge() {
		return this.knowledgeProvider.getLocalKnowledgeData();
	}

}
