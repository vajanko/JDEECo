package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Collection;

import cz.cuni.mff.d3s.jdeeco.gossip.buffer.HeaderPayload;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ItemHeader;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * Broadcasts headers of missing or outdated messages.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendPLPlugin extends SendBasePlugin {
	
	public static final String TASK_PERIOD = "deeco.sendPL.period";
	public static final long TASK_PERIOD_DEFAULT = 1000;
	
	public SendPLPlugin() {
		super(Long.getLong(TASK_PERIOD, TASK_PERIOD_DEFAULT));
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		
		PacketHeader header = new PacketHeader(L2PacketType.PULL_REQUEST);
		
		// check whether there are some missing or outdated messages and if yes send a PULL request
		Collection<ItemHeader> missingMessages = receptionBuffer.getLocallyObsoleteItems(time);
		
		// remove local knowledge from the request
		if (!missingMessages.isEmpty()) {
			HeaderPayload data = new HeaderPayload(missingMessages);
			L2Packet packet = new L2Packet(header, data);
			
			for (Address address : this.addressRegister.getAddresses())
				this.networkLayer.sendL2Packet(packet, address);
		}
	}
}
