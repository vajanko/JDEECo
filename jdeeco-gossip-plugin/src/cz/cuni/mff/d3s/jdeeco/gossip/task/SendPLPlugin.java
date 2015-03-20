package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Collection;

import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.HeaderPayload;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ItemHeader;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * Broadcasts headers of missing or outdated messages.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendPLPlugin extends SendBasePlugin {
	
	public SendPLPlugin() {
		super(GossipProperties.getKnowledgePullPeriod());
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		
		PacketHeader header = new PacketHeader(L2PacketType.PULL_REQUEST);
		
		// check whether there are some missing or outdated messages and if yes send a PULL request
		Collection<ItemHeader> missingMessages = messageBuffer.getObsoletePushedItems(time);
		if (!missingMessages.isEmpty()) {
			HeaderPayload data = new HeaderPayload(missingMessages);
			L2Packet packet = new L2Packet(header, data);
			
			networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
		}
	}
}
