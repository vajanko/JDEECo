/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

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
	
	static class ProtocolParam {
		public String name;
		public Function<L2Packet, String> getter;
		
		public ProtocolParam(String name, Function<L2Packet, String> getter) {
			this.name = name;
			this.getter = getter;
		}
	}
	
	public static final String LOGGER_OUT = "deeco.requestLogger.out";
	public static final String LOGGER_OUT_DEFAULT = "console";
	
	private KnowledgeManagerContainer kmContainer;
	private ReceptionBuffer receptionBuffer;
	private CurrentTimeProvider timeProvider;
	private Layer1 layer1;
	
	// log parameters
	private int nodeId;
	private long time;	// time of message being sent
	private String actionType;	// SEND, RECV
	private String messageType;	// KN, HD, PL
	
	private List<ProtocolParam> parameters = new ArrayList<ProtocolParam>();
	private static List<ProtocolParam> staticParameters = new ArrayList<ProtocolParam>();
	
	private static boolean headerInitialized = false;
	private void printHeader() {
		if (headerInitialized)
			return;
		
		outputStream.print("Node;Time;Action;Type");
		for (ProtocolParam param : parameters)
			outputStream.print(";" + param.name);
		outputStream.println();
		headerInitialized = true;
	}
	
	private static PrintStream outputStream = null;
	public static void initOutputStream(PrintStream outputStream) {
		RequestLoggerPlugin.outputStream = outputStream;
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
	
	public static void registerStatParam(String name, Function<L2Packet, String> getter) {
		staticParameters.add(new ProtocolParam(name, getter));
	}
	public void registerParam(String name, Function<L2Packet, String> getter) {
		parameters.add(new ProtocolParam(name, getter));
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
	private void printRequest(String action, L2Packet packet) {
		
		printHeader();
		
		L2PacketType type = packet.header.type;
		this.messageType = getMessageType(type);
		this.actionType = action;
		this.time = timeProvider.getCurrentMilliseconds();
		
		outputStream.print(String.format("%d;%d;%s;%s", nodeId, time, actionType, messageType));
		for (ProtocolParam param : parameters)
			outputStream.print(";" + param.getter.apply(packet));
		outputStream.println();
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			KnowledgeData kd = (KnowledgeData)packet.getObject();
			KnowledgeMetaData meta = kd.getMetaData();
			
			if (!receptionBuffer.canReceive(meta.componentId, meta.versionId))
				return;
			
			if (kmContainer.hasLocal(meta.componentId))
				return;
		}
		
		printRequest("RECV", packet);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l1.L2PacketSender#sendL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet, cz.cuni.mff.d3s.jdeeco.network.address.Address)
	 */
	@Override
	public boolean sendL2Packet(L2Packet packet, Address address) {
		
		// just pass the packet to the underlying layer as would be done
		// previously by L2 layer
		boolean res = layer1.sendL2Packet(packet, address);
		
		if (res)
			printRequest("SEND", packet);
		
		return res;
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
		this.receptionBuffer = container.getPluginInstance(ReceptionBuffer.class);
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

		this.parameters.addAll(staticParameters);
	}
	
}
