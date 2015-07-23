/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.receive;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.jdeeco.gossip.ItemHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageHeader;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.MarshallerRegistry;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.SerializingMarshaller;

/**
 * Strategy for processing headers of pulled messages.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class ReceivePLPlugin extends ReceiveBasePlugin {
		
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.PULL_REQUEST)) {
			// header of missing messages are pulled to be rebroadcasted
			receive((MessageHeader)packet.getObject());
		}
	}
	/**
	 * Receive pull request on the current node.
	 * 
	 * @param header Pull request to be received.
	 */
	private void receive(MessageHeader header) {
		for (ItemHeader hd : header.getHeaders()) {
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
			receptionBuffer.receivePull(hd.id, hd.timestamp);
			//messageBuffer.receiveGlobal(header.id, header.timestamp);
			
			// make this knowledge as PULLed, will be broadcasted when present
			//messageBuffer.notifyPull(header.id);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceiveBasePlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		super.init(container);
		
		Network network = container.getPluginInstance(Network.class);
		MarshallerRegistry registry = network.getL2().getMarshallers();
		registry.registerMarshaller(L2PacketType.PULL_REQUEST, new SerializingMarshaller());
	}
}
