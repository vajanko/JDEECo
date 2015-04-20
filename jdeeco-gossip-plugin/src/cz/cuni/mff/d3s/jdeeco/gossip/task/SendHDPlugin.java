package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Collection;
import java.util.Iterator;

import cz.cuni.mff.d3s.jdeeco.gossip.buffer.HeaderPayload;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ItemHeader;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * Broadcasts headers of received messages on the current node.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendHDPlugin extends SendBasePlugin {
	
	public static final String TASK_PERIOD = "deeco.sendHD.period";
	/**
	 * Default value of message headers broadcasting period in milliseconds.
	 */
	public static final long TASK_PERIOD_DEFAULT = 2000;
	
	public SendHDPlugin() {
		super(Long.getLong(TASK_PERIOD, TASK_PERIOD_DEFAULT));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		
		PacketHeader header = new PacketHeader(L2PacketType.MESSAGE_HEADERS);
		
		// get messages received by current node or messages received by other nodes
		// which notified about these messages
		Collection<ItemHeader> headers = receptionBuffer.getRecentItems(time);
		
		// remove headers of messages sent from current node
		// it is not necessary to send them because the message itself will be broadcasted
		Iterator<ItemHeader> it = headers.iterator();
		while (it.hasNext()) {
			ItemHeader hd = it.next();
			if (knowledgeProvider.hasLocal(hd.id))
				it.remove();
		}
		
		if (!headers.isEmpty()) {
			HeaderPayload data = new HeaderPayload(headers);
			L2Packet packet = new L2Packet(header, data);

			sendPacket(packet);
		}
	}
}
