/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog.ActType;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog.MsgType;
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
	
	public static void printRequest(int node, long time, MsgType msgType, ActType actType, Object data) {
		System.out.println(String.format("[%d] %4d %s %s %s", node, time, msgType, actType, data));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
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
		ConsoleLog.printRequest(nodeId, time, msgType, ActType.RECV, data);
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
