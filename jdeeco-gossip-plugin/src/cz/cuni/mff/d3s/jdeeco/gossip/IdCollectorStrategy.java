/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class IdCollectorStrategy implements L2Strategy {

	private Map<String, Long> ids = new HashMap<String, Long>();
	private CurrentTimeProvider timeProvider;
	
	/**
	 * 
	 */
	public IdCollectorStrategy(CurrentTimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			KnowledgeData kd = (KnowledgeData)packet.getObject();
			
			// Collect all ids of component which are coming from the network.
			// Remember also the time so that we can recognise when some component
			// is lost and does not participate in the communication any more.
			String id = kd.getMetaData().componentId;
			long time = timeProvider.getCurrentMilliseconds();
			
			ids.put(id, time);
		}
		else if (packet.header.type.equals(L2PacketType.MESSAGE_HEADERS)) {
			
		}
	}

}
