package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

/**
 * Broadcasts local knowledge data.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendPushedKNPlugin extends SendBaseKNPlugin {
	
	public static final String TASK_PERIOD = "deeco.sendPushedKN.period";
	/**
	 * Default value of knowledge broadcasting period in milliseconds.
	 */
	public static final long TASK_PERIOD_DEFAULT = 2000;
	
	public SendPushedKNPlugin() {
		super(Long.getLong(TASK_PERIOD, TASK_PERIOD_DEFAULT));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.task.SendBaseKNPlugin#getKnowledgeData()
	 */
	@Override
	protected Collection<KnowledgeData> getKnowledgeData(long currentTime) {
		return knowledgeProvider.getLocalKnowledgeData();
	}
}
