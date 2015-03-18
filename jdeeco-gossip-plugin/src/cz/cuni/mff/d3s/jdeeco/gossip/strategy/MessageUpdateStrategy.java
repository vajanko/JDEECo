/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.PushHeadersPayload;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MessageUpdateStrategy implements L2Strategy, DEECoPlugin {

	private MessageBuffer messageBuffer;
	private CurrentTimeProvider timeProvider;
	private int nodeId;
		
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

			System.out.println(String.format("[%2d] %4d KN RECV [%s]", nodeId, time, id));
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
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, MessageBuffer.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.timeProvider = container.getRuntimeFramework().getScheduler().getTimer();
		this.messageBuffer = container.getPluginInstance(MessageBuffer.class);
		this.nodeId = container.getId();
		
		// register L2 strategy
		container.getPluginInstance(Network.class).getL2().registerL2Strategy(this);
	}

}
