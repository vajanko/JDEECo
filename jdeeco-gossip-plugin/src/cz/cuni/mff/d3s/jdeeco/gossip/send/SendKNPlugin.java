/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.send;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.jdeeco.gossip.BasicKnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendKNPlugin extends SendBasePlugin {
	
	public static final String TASK_PERIOD = "deeco.sendKN.period";
	/**
	 * Default value of knowledge broadcasting period in milliseconds.
	 */
	public static final long TASK_PERIOD_DEFAULT = 2000;
	
	private KnowledgeSource knowledgeSource;
	
	public void setKnowledgeSource(KnowledgeSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
	}
	
	public SendKNPlugin() {
		super(Long.getLong(TASK_PERIOD, TASK_PERIOD_DEFAULT));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		for(KnowledgeData data: knowledgeSource.getKnowledge()) {
			PacketHeader header = new PacketHeader(L2PacketType.KNOWLEDGE);
			
			L2Packet packet = new L2Packet(header, data);
			
			// send knowledge ...
			// TODO: select recipients
			sendPacket(packet);
			
			// ... and stores information about last knowledge version
			KnowledgeMetaData meta = data.getMetaData();
			receptionBuffer.receiveLocal(meta.componentId, meta.createdAt, meta.versionId);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.send.SendBasePlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		super.init(container);
		
		this.knowledgeSource = new BasicKnowledgeSource(this.knowledgeProvider);
	}
}
