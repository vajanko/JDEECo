/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.l1.L2PacketSender;
import cz.cuni.mff.d3s.jdeeco.network.l1.Layer1;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;

/**
 * Plugin for debugging purpose only. Allows for logging of sent and received
 * messages.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class RequestLoggerPlugin implements L2Strategy, L2PacketSender, DEECoPlugin {
	
	private CurrentTimeProvider timeProvider;
	private Layer1 layer1;
	private int nodeId;
	private PrintStream outputStream;
	
	public RequestLoggerPlugin(PrintStream outputStream) {
		this.outputStream = outputStream;
	}
	public RequestLoggerPlugin() {
		this(System.out);
	}
	
	private static String getMessageType(L2PacketType type) {
		if (type.equals(L2PacketType.KNOWLEDGE))
			return "KN";
		else if (type.equals(L2PacketType.MESSAGE_HEADERS))
			return "HD";
		else if (type.equals(L2PacketType.PULL_REQUEST))
			return "PL";

		return "";
	}
	private void printRequest(String action, L2PacketType type, Object data) {
		long time = timeProvider.getCurrentMilliseconds();
		String msgType = getMessageType(type);
		outputStream.println(String.format("[%d];%4d;%s;%s;%s", nodeId, time, action, msgType, data));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		printRequest("RECV", packet.header.type, packet.getObject());
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l1.L2PacketSender#sendL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet, cz.cuni.mff.d3s.jdeeco.network.address.Address)
	 */
	@Override
	public boolean sendL2Packet(L2Packet packet, Address address) {
		
		printRequest("SEND", packet.header.type, packet.getObject());
		
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
		// dependencies
		this.timeProvider = container.getRuntimeFramework().getScheduler().getTimer();
		this.nodeId = container.getId();
		
		Network net = container.getPluginInstance(Network.class);
		
		// replace standard sender of layer 2
		net.getL2().setL2PacketSender(this);
		// but the underlying layer 1 will be used
		this.layer1 = net.getL1();
		
		// register L2 strategy
		net.getL2().registerL2Strategy(this);
		
		//if (nodeId == 1)
		//	System.out.println("Node;Time;Action;Type;Data");
	}
	
}
