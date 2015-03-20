package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;

/**
 * Strategy for processing received knowledge message. Message header is extracted
 * and stored to local buffer.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ReceiveKNStrategy extends BaseReceiveStrategy {
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			KnowledgeData kd = (KnowledgeData)packet.getObject();
			
			// Collect all IDs of component which are coming from the network.
			// Remember also the time so that we can recognise when some component
			// is lost and does not participate in the communication any more.
			KnowledgeMetaData meta = kd.getMetaData();

			// TODO: should message received from current node be excluded ??
			messageBuffer.notifyLocalPush(meta.componentId, meta.createdAt);
		}
	}
}