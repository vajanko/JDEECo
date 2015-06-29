package cz.cuni.mff.d3s.jdeeco.gossip.receive;

import cz.cuni.mff.d3s.jdeeco.gossip.buffer.MessageHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ItemHeader;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;

/**
 * Strategy for processing headers of received messages. Received headers
 * are stored in a buffer.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ReceiveHDStrategy extends ReceiveBaseStrategy {
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		if (packet.header.type.equals(L2PacketType.MESSAGE_HEADERS)) {
			// Message headers from other nodes are not re-broadcasted but they
			// are combined with headers known by this node and then gossipped together.
			receive((MessageHeader)packet.getObject());
		}
	}
	
	/**
	 * Process received HD message.
	 * 
	 * @param header Deserialised instance of HD message
	 */
	private void receive(MessageHeader header) {
		for (ItemHeader hd : header.getHeaders()) {
			receptionBuffer.receiveGlobal(hd.id, hd.timestamp);
		}
	}
}

