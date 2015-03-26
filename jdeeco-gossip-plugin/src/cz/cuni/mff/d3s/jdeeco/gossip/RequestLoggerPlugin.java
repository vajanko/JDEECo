/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerContainer;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ReceptionBuffer;
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
	
	public static final String LOGGER_OUT = "deeco.requestLogger.out";
	public static final String LOGGER_OUT_DEFAULT = "console";
	public static final String LOGGER_ARG1 = "deeco.requestLogger.arg1";
	public static final String LOGGER_ARG2 = "deeco.requestLogger.arg2";
	public static final String LOGGER_ARG3 = "deeco.requestLogger.arg3";
	
	private KnowledgeManagerContainer kmContainer;
	private ReceptionBuffer messageBuffer;
	private CurrentTimeProvider timeProvider;
	private Layer1 layer1;
	private int nodeId;
	
	private String arg1;
	private String arg2;
	private String arg3;
	private long lastKnowledgeUpdate = 0;
	
	private static PrintStream outputStream = null;
	public static void initOutputStream(PrintStream outputStream) {
		RequestLoggerPlugin.outputStream = outputStream;
		// print header
		outputStream.println("Node;Time;Action;Type;Data;Arg1;Arg2;Arg3");
	}
	public static void initOutputStream(String filename) throws FileNotFoundException {
		initOutputStream(new PrintStream(filename));
	}
	public static void initOutputStream() {
		if (outputStream != null)
			return;		// this is a singleton instance
		
		String logger = System.getProperty(LOGGER_OUT, LOGGER_OUT_DEFAULT);
		if (logger.equalsIgnoreCase("console")) {
			initOutputStream(System.out);
		}
		else {
			try {
				initOutputStream(logger);
			} catch (FileNotFoundException e) { }
		}
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
		
		if (type.equals(L2PacketType.KNOWLEDGE)) {
			KnowledgeData kd = (KnowledgeData)data;
			arg2 = kd.getMetaData().componentId;
			arg3 = String.valueOf(time - lastKnowledgeUpdate);
			
			lastKnowledgeUpdate = kd.getMetaData().createdAt;
		}
		
		outputStream.println(String.format("%d;%4d;%s;%s;%s;%s;%s;%s", 
				nodeId, time, action, msgType, data, arg1, arg2, arg3));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			KnowledgeData kd = (KnowledgeData)packet.getObject();
			KnowledgeMetaData meta = kd.getMetaData();
			
			if (!messageBuffer.canReceive(meta.componentId, meta.versionId))
				return;
			
			if (kmContainer.hasLocal(meta.componentId))
				return;
		}
		
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
		return Arrays.asList(Network.class, ReceptionBuffer.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// dependencies
		this.kmContainer = container.getRuntimeFramework().getContainer();
		this.messageBuffer = container.getPluginInstance(ReceptionBuffer.class);
		this.timeProvider = container.getRuntimeFramework().getScheduler().getTimer();
		this.nodeId = container.getId();
		
		Network net = container.getPluginInstance(Network.class);
		
		// replace standard sender of layer 2
		net.getL2().setL2PacketSender(this);
		// but the underlying layer 1 will be used
		this.layer1 = net.getL1();
		
		// register L2 strategy
		net.getL2().registerL2Strategy(this);
		
		// initialise stream for logger output
		initOutputStream();
		
		this.arg1 = System.getProperty(LOGGER_ARG1);
		this.arg2 = System.getProperty(LOGGER_ARG2);
		this.arg3 = System.getProperty(LOGGER_ARG3);
	}
	
}
