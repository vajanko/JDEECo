/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PacketLoggerStrategy implements L2Strategy, DEECoPlugin {

	private CurrentTimeProvider timeProvider;
	private int nodeId;
	
	private void printReceive(L2PacketType type, Object data) {
		String msgType = "??";
		
		if (type.equals(L2PacketType.KNOWLEDGE)) {
			msgType = "KN";
		}
		else if (type.equals(L2PacketType.MESSAGE_HEADERS)) {
			msgType = "HD";
		}
		else if (type.equals(L2PacketType.PULL_REQUEST)) {
			msgType = "PL";
		}
		
		long time = timeProvider.getCurrentMilliseconds();
		System.out.println(String.format("[%d];%4d;RECV;%s;%s", nodeId, time, msgType, data));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		printReceive(packet.header.type, packet.getObject());
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
		
		// register L2 strategy
		Layer2 networkLayer = container.getPluginInstance(Network.class).getL2();
		networkLayer.registerL2Strategy(this);
	}

	

}
