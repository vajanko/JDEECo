package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;

/**
 * Broadcasts local knowledge data.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendPushedKNPlugin extends SendBaseKNPlugin {
	
	public SendPushedKNPlugin() {
		super(GossipProperties.getKnowledgePushPeriod());
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.task.SendBaseKNPlugin#getKnowledgeData()
	 */
	@Override
	protected Collection<KnowledgeData> getKnowledgeData(long currentTime) {
		return knowledgeProvider.getLocalKnowledgeData();
	}
}
