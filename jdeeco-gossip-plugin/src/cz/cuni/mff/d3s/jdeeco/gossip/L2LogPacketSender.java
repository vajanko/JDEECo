/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog.ActType;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog.MsgType;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.l1.L2PacketSender;
import cz.cuni.mff.d3s.jdeeco.network.l1.Layer1;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class L2LogPacketSender implements L2PacketSender, DEECoPlugin {

	private Layer1 layer1;
	private CurrentTimeProvider timeProvider;
	private int nodeId;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l1.L2PacketSender#sendL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet, cz.cuni.mff.d3s.jdeeco.network.address.Address)
	 */
	@Override
	public boolean sendL2Packet(L2Packet packet, Address address) {
		MsgType msgType = MsgType.KN;
		Object data = packet.getObject();
		
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			msgType = MsgType.KN;
		}
		else if (packet.header.type.equals(L2PacketType.MESSAGE_HEADERS)) {
			msgType = MsgType.HD;
		}
		else if (packet.header.type.equals(L2PacketType.PULL_REQUEST)) {
			msgType = MsgType.PL;
		}
		
		long time = timeProvider.getCurrentMilliseconds();
		ConsoleLog.printRequest(nodeId, time, msgType, ActType.SEND, data);
		
		// just pass the packet to the underlying layer as would be done
		// previously by L2 layer
		return layer1.sendL2Packet(packet, address);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.timeProvider = container.getRuntimeFramework().getScheduler().getTimer();
		this.nodeId = container.getId();
		
		Network net = container.getPluginInstance(Network.class);
		
		// replace standard sender of layer 2
		net.getL2().setL2PacketSender(this);
		
		// but the underlying layer 1 will be used
		this.layer1 = net.getL1();
	}
}
