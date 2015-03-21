/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ItemHeader;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendPulledKNPlugin extends SendBaseKNPlugin {

	public SendPulledKNPlugin() {
		// TODO: name this parameter properly
		super(GossipProperties.getHeadersPushPeriod());
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.task.SendKNBasePlugin#getKnowledgeData()
	 */
	@Override
	protected Collection<KnowledgeData> getKnowledgeData(long currentTime) {
		//Collection<ItemHeader> pulledItems = messageBuffer.getRecentPulledMessages(currentTime);
		ArrayList<KnowledgeData> result = new ArrayList<KnowledgeData>();
		/*for (ItemHeader item : pulledItems) {
			KnowledgeData kd = knowledgeProvider.getComponentKnowledge(item.id);
			if (kd != null) {
				result.add(kd);
				
				messageBuffer.notifyPull(item.id, Long.MIN_VALUE);
			}
		}*/
		
		return result;
	}

}
