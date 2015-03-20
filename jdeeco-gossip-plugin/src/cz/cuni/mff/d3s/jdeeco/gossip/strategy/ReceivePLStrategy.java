/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import cz.cuni.mff.d3s.jdeeco.gossip.buffer.HeaderPayload;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ItemHeader;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;

/**
 * Strategy for processing headers of pulled messages.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ReceivePLStrategy extends BaseReceiveStrategy {
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.PULL_REQUEST)) {
			// header of missing messages are pulled to be rebroadcasted
			HeaderPayload messages = (HeaderPayload)packet.getObject();
			
			for (ItemHeader header : messages.getHeaders()) {
				messageBuffer.notifyGlobalPull(header.id, header.timestamp);
			}
		}
	}
}
