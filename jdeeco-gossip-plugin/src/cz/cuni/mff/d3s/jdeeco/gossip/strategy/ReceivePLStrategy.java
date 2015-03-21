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
public class ReceivePLStrategy extends ReceiveBaseStrategy {
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.PULL_REQUEST)) {
			// header of missing messages are pulled to be rebroadcasted
			HeaderPayload messages = (HeaderPayload)packet.getObject();
			
			for (ItemHeader header : messages.getHeaders()) {
				// PULL request from other node:
				// 1) knowledge with ID is not present on this node:
				//	this will result in adding a header with local reception -INFINITE 
				//	and this knowledge will be PULLed also with the current node
				// 2a) knowledge with ID is present on this node:
				//	local and global reception stay unchanged, knowledge can be sent
				//	from this node and the PULL request finished here.
				// 2b) knowledge with ID is not present on this node:
				//	local reception stays unchanged and a max value will be taken for
				//	the global reception.
				//	this knowledge will be PULLed as well
				messageBuffer.receiveGlobal(header.id, header.timestamp);
				
				// make this knowledge as PULLed, will be broadcasted when present
				messageBuffer.notifyPull(header.id);
			}
		}
	}
}
