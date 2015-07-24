package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Collection;
import java.util.Iterator;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.jdeeco.gossip.ItemHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageHeader;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.MarshallerRegistry;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.SerializingMarshaller;

/**
 * Broadcasts headers of received messages on the current node.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class SendHDPlugin extends SendBasePlugin {
	
	/**
	 * Configuration property name of message headers publish period.
	 */
	public static final String TASK_PERIOD = "deeco.sendHD.period";
	/**
	 * Default value of message headers broadcasting period in milliseconds.
	 */
	public static final long TASK_PERIOD_DEFAULT = 2000;
	
	/**
	 * Creates a new instance of plugin sending headers of received messages
	 * initialised with period from configuration file.
	 */
	public SendHDPlugin() {
		super(Long.getLong(TASK_PERIOD, TASK_PERIOD_DEFAULT));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		
		publish(time);

		// PULL request on IP network is not implemented
	}
	/**
	 * Publishes header of received messages on the network.
	 * 
	 * @param time Current system or simulation time.
	 */
	private void publish(long time) {
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
		
		if (headers.isEmpty())
			return;
		
		MessageHeader data = new MessageHeader(headers);
		L2Packet packet = new L2Packet(header, data);
		
		this.networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.send.SendBasePlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		super.init(container);
		
		Network network = container.getPluginInstance(Network.class);
		MarshallerRegistry registry = network.getL2().getMarshallers();
		registry.registerMarshaller(L2PacketType.MESSAGE_HEADERS, new SerializingMarshaller());
	}
}
