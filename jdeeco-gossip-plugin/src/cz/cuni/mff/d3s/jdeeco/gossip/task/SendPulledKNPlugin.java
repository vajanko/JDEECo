/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendPulledKNPlugin extends SendBaseKNPlugin {
	public static final String TASK_PERIOD = "deeco.sendPulledKN.period";
	/**
	 * Default value of knowledge broadcasting period in milliseconds.
	 */
	public static final long TASK_PERIOD_DEFAULT = 2000;
	
	public SendPulledKNPlugin() {
		super(Long.getLong(TASK_PERIOD, TASK_PERIOD_DEFAULT));
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
				
				// this is necessary only when responding to PULL request on local data
				KnowledgeMetaData meta = kd.getMetaData();
				messageBuffer.receiveLocal(meta.componentId, meta.createdAt);
			}
		}
		
		for (KnowledgeData kd : result) {
			messageBuffer.clearPulledTag(kd.getMetaData().componentId);
		}
		
		return result;
	}

}
