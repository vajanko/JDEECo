/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.PushHeadersPayload;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MessageUpdateStrategy implements L2Strategy {

	private MessageBuffer messageBuffer;
	private CurrentTimeProvider timeProvider;
	
	/**
	 * 
	 */
	public MessageUpdateStrategy(RuntimeFramework runtime, GossipPlugin gossip) {
		this.messageBuffer = gossip.getMessageBuffer();
		this.timeProvider = runtime.getScheduler().getTimer();
	}
	
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
			String id = kd.getMetaData().componentId;
			long time = timeProvider.getCurrentMilliseconds();

			messageBuffer.localUpdate(id, time);
		}
		else if (packet.header.type.equals(L2PacketType.MESSAGE_HEADERS)) {
			// Message headers from other nodes are not re-broadcasted but they
			// are combined with headers known by this node and then gossipped together.
			PushHeadersPayload msgs = (PushHeadersPayload)packet.getObject();
			
			for (MessageHeader header : msgs.getHeaders()) {
				messageBuffer.globalUpdate(header.id, header.timestamp);
			}
		}
	}

}
