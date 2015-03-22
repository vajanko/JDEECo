/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendPulledKNPlugin extends SendBaseKNPlugin {

	public SendPulledKNPlugin() {
		// TODO: name this parameter properly
		super(GossipProperties.getPublishHDPeriod());
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.task.SendKNBasePlugin#getKnowledgeData()
	 */
	@Override
	protected Collection<KnowledgeData> getKnowledgeData(long currentTime) {
		ArrayList<KnowledgeData> result = new ArrayList<KnowledgeData>();
		for (String id : messageBuffer.getPulledItems()) {
			KnowledgeData kd = knowledgeProvider.getComponentKnowledge(id);
			if (kd != null) {
				result.add(kd);
			}
		}
		
		messageBuffer.clearPulledItems();
		
		return result;
	}

}
