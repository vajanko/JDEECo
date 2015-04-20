/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public abstract class SendBaseKNPlugin extends SendBasePlugin {
	/**
	 * @param period
	 */
	public SendBaseKNPlugin(long period) {
		super(period);
	}
	
	protected abstract Collection<KnowledgeData> getKnowledgeData(long currentTime);
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		for(KnowledgeData data: getKnowledgeData(time)) {
			PacketHeader header = new PacketHeader(L2PacketType.KNOWLEDGE);
			L2Packet packet = new L2Packet(header, data);
			
			// broadcast knowledge ...
			networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
			
			// ... and stores information about last knowledge version
			KnowledgeMetaData meta = data.getMetaData();
			receptionBuffer.receiveLocal(meta.componentId, meta.createdAt, meta.versionId);
		}
	}
}
