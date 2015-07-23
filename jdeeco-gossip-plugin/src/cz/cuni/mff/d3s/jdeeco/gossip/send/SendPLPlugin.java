package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Collection;

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
 * Broadcasts headers of missing or outdated messages.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendPLPlugin extends SendBasePlugin {
	
	/**
	 * Configuration property name of pull request check period.
	 */
	public static final String TASK_PERIOD = "deeco.sendPL.period";
	/**
	 * Default value of pull request check period in milliseconds.
	 */
	public static final long TASK_PERIOD_DEFAULT = 1000;
	
	/**
	 * Creates a new instance of plugin sending pull request initialised with period from 
	 * configuration file.
	 */
	public SendPLPlugin() {
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
	private void publish(long time) {
		PacketHeader header = new PacketHeader(L2PacketType.PULL_REQUEST);
		
		// check whether there are some missing or outdated messages and if yes send a PULL request
		Collection<ItemHeader> missingMessages = receptionBuffer.getLocallyObsoleteItems(time);
		
		// remove local knowledge from the request
		if (missingMessages.isEmpty())
			return; 
				
		MessageHeader data = new MessageHeader(missingMessages);
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
		registry.registerMarshaller(L2PacketType.PULL_REQUEST, new SerializingMarshaller());
	}
}
