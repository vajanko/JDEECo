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
import cz.cuni.mff.d3s.jdeeco.gossip.receive.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendPLPlugin;
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
	public static final String LOGGER_VARIANT = "deeco.requestLogger.variant";
	public static final String LOGGER_ARG1 = "deeco.requestLogger.arg1";
	public static final String LOGGER_ARG2 = "deeco.requestLogger.arg2";
	public static final String LOGGER_ARG3 = "deeco.requestLogger.arg3";
	
	private KnowledgeManagerContainer kmContainer;
	private ReceptionBuffer receptionBuffer;
	private CurrentTimeProvider timeProvider;
	private Layer1 layer1;
	
	// log parameters
	private String variant;		// name of the algorithm - could include enabled features etc.
	private int nodeId;
	private String messageType;	// KN, HD, PL
	private String actionType;	// SEND, RECV
	private long time;	// time of message being sent
	
	// knowledge specific
	private Long knowledgeAge;
	private String componentId;
	private int isSource;	// 1 if message is sending its own knowledge (not re-broadcasting or re-sending replica), 0 for rebroadcast, -1 otherwise
	
	// communication protocol arguments
	private Long hdPeriod;
	private Long plPeriod;
	private Long knPeriod;
	private Double probability;
	private Long localTimeout;
	private Long globalTimeout;
	
//	private String arg1;
//	private String arg2;
//	private String arg3;
	
	private static PrintStream outputStream = null;
	public static void initOutputStream(PrintStream outputStream) {
		RequestLoggerPlugin.outputStream = outputStream;
		// print header
		outputStream.println("Variant;Node;Time;Action;Type;Component;Age;IsSource;HDPeriod;PLPeriod;KNPeriod;Probability;"
				+ "LocalTimeout;GlobalTimeout");
				//+ "Arg1;Arg2;Arg3");
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
		
		this.messageType = getMessageType(type);
		this.actionType = action;
		this.time = timeProvider.getCurrentMilliseconds();
		
		
		if (type.equals(L2PacketType.KNOWLEDGE)) {
			KnowledgeData kd = (KnowledgeData)data;
			KnowledgeMetaData meta = kd.getMetaData();
			this.componentId = meta.componentId;
			long lastUpdate = receptionBuffer.getLocalReceptionTime(meta.componentId);
			lastUpdate = lastUpdate == ReceptionBuffer.MINUS_INFINITE ? 0 : lastUpdate;
			this.knowledgeAge = (time - lastUpdate);
			this.isSource = meta.componentId.endsWith(meta.sender) ? 1 : 0;
		}
		else {
			this.componentId = null;
			this.knowledgeAge = -1l;
			this.isSource = -1;
		}
		
		outputStream.println(String.format("%s;%d;%d;%s;%s;"+ "%s;%d;%s;" + "%s;%s;%s;" + "%s;%s;%s", 
				variant, nodeId, time, actionType, messageType,
				componentId, knowledgeAge, isSource,
				hdPeriod != null ? hdPeriod : "",plPeriod != null ? plPeriod : "",knPeriod != null ? knPeriod : "",
				probability != null ? probability : "",localTimeout != null ? localTimeout : "",globalTimeout != null ? globalTimeout : ""));
				//arg1 != null ? arg1 : "", arg2 != null ? arg2 : "", arg3 != null ? arg3 : ""));
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
		
		this.variant = System.getProperty(LOGGER_VARIANT);
		this.hdPeriod = Long.getLong(SendHDPlugin.TASK_PERIOD, SendHDPlugin.TASK_PERIOD_DEFAULT);
		this.plPeriod = Long.getLong(SendPLPlugin.TASK_PERIOD, SendPLPlugin.TASK_PERIOD_DEFAULT);
		this.knPeriod = Long.getLong(SendKNPlugin.TASK_PERIOD, SendKNPlugin.TASK_PERIOD_DEFAULT);
		this.probability = Double.parseDouble(System.getProperty(GossipRebroadcastStrategy.REBROADCAST_PROBABILITY));
		this.localTimeout = Long.getLong(ReceptionBuffer.LOCAL_TIMEOUT, ReceptionBuffer.LOCAL_TIMEOUT_DEFAULT);
		this.globalTimeout = Long.getLong(ReceptionBuffer.GLOBAL_TIMEOUT, ReceptionBuffer.GLOBAL_TIMEOUT_DEFAULT);
		
//		this.arg1 = System.getProperty(LOGGER_ARG1);
//		this.arg2 = System.getProperty(LOGGER_ARG2);
//		this.arg3 = System.getProperty(LOGGER_ARG3);
	}
	
}
