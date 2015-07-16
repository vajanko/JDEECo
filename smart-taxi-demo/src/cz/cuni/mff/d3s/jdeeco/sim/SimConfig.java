/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.deeco.timer.DiscreteEventTimer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.core.EnsembleDeployerPlugin;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeProviderPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.ItemHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.GossipRebroadcastPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceiveHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceiveKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceivePLPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.RangeRecipientSelector;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendPLPlugin;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperAddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperIntervalRange;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRange;
import cz.cuni.mff.d3s.jdeeco.grouper.RangeConfigurator;
import cz.cuni.mff.d3s.jdeeco.grouper.client.GrouperClientPlugin;
import cz.cuni.mff.d3s.jdeeco.grouper.server.GrouperServerPlugin;
import cz.cuni.mff.d3s.jdeeco.grouper.server.PartitionedByProcessor;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
import cz.cuni.mff.d3s.jdeeco.matsomn.MatsomnPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTBroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTInfrastructureDevice;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SimConfig {
	
	static class StaticGetter implements Function<L2Packet, String> {
		
		private Object value;

		public StaticGetter(String property) {
			this.value = System.getProperty(property, "");
		}
		
		/* (non-Javadoc)
		 * @see java.util.function.Function#apply(java.lang.Object)
		 */
		@Override
		public String apply(L2Packet t) {
			return value.toString();
		}
	}
	static class ComponentIdGetter implements Function<L2Packet, String> {	
		/* (non-Javadoc)
		 * @see java.util.function.Function#apply(java.lang.Object)
		 */
		@Override
		public String apply(L2Packet t) {
			if (t.header.type.equals(L2PacketType.KNOWLEDGE)) {
				KnowledgeData kd = (KnowledgeData)t.getObject();
				return kd.getMetaData().componentId;
			}
			else if (t.header.type.equals(L2PacketType.PULL_REQUEST)) {
				MessageHeader hd = (MessageHeader)t.getObject();
				StringBuilder str = new StringBuilder();
				for (ItemHeader it : hd.getHeaders()) {
					str.append(it.id + ", ");
				}
				return str.substring(0, str.length() - 2);
			}
			
			return "";
		}
	}
	static class SourceGetter implements Function<L2Packet, String> {

		/* (non-Javadoc)
		 * @see java.util.function.Function#apply(java.lang.Object)
		 */
		@Override
		public String apply(L2Packet t) {
			if (!t.header.type.equals(L2PacketType.KNOWLEDGE))
				return "";
			
			KnowledgeMetaData meta = ((KnowledgeData)t.getObject()).getMetaData();
			return meta.componentId.endsWith(meta.sender) ? "1" : "0";
		}
		
	}
	public static class KnowledgeAgeGetter implements Function<L2Packet, String>, DEECoPlugin {

		private CurrentTimeProvider timeProvider;
		private ReceptionBuffer receptionBuffer;
		
		
		
		/* (non-Javadoc)
		 * @see java.util.function.Function#apply(java.lang.Object)
		 */
		@Override
		public String apply(L2Packet t) {
			if (!t.header.type.equals(L2PacketType.KNOWLEDGE))
				return "";
			
			KnowledgeData kd = (KnowledgeData)t.getObject();
			long time = timeProvider.getCurrentMilliseconds();
			long lastUpdate = receptionBuffer.getLocalReceptionTime(kd.getMetaData().componentId);
			lastUpdate = lastUpdate == ReceptionBuffer.MINUS_INFINITE ? 0 : lastUpdate;
			
			return String.valueOf(time - lastUpdate);
		}

		/* (non-Javadoc)
		 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
		 */
		@Override
		public List<Class<? extends DEECoPlugin>> getDependencies() {
			return Arrays.asList(ReceptionBuffer.class, RequestLoggerPlugin.class);
		}

		/* (non-Javadoc)
		 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
		 */
		@Override
		public void init(DEECoContainer container) {
			this.timeProvider = container.getRuntimeFramework().getScheduler().getTimer();
			this.receptionBuffer = container.getPluginInstance(ReceptionBuffer.class);
			
			RequestLoggerPlugin logger = container.getPluginInstance(RequestLoggerPlugin.class);
			logger.registerParam("Age", this);
		}
	}
	
	public static class DefaultRangeConfigurator implements RangeConfigurator {

		private int count = 0;
		private int groupers;
		
		public DefaultRangeConfigurator(int groupers) {
			this.groupers = groupers;
		}
		
		/* (non-Javadoc)
		 * @see cz.cuni.mff.d3s.jdeeco.grouper.RangeConfigurator#getRange(cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition, int)
		 */
		@Override
		public GrouperRange getRange(EnsembleDefinition ensemble, int nodeId) {
			
			// totally there are 100 sectors
			int step = 100 / groupers;
			
			GrouperRange range = new GrouperIntervalRange(count * step, (count + 1) * step - 1);
			count++;
			
			return range;
		}
	}
//	static class EmptyRangeConfigurator implements RangeConfigurator {
//
//		/* (non-Javadoc)
//		 * @see cz.cuni.mff.d3s.jdeeco.grouper.RangeConfigurator#getRange(cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition, int)
//		 */
//		@Override
//		public GrouperRange getRange(EnsembleDefinition ensemble, int nodeId) {
//			return new GrouperIntervalRange(0,0);
//		}
//		
//	}
	private static RangeConfigurator rangeConfig;
	
	
	public static final String GOSSIP_FEATURES = "deeco.sim.features";
	public static final String GOSSIP_FEATURES_DEFAULT = "";
	
//	public static final String DEPLOY_ENSEMBLES = "deeco.sim.ensembles";
//	public static final String DEPLOY_ENSEMBLES_DEFAULT = "";
	
	public static final String GOSSIP_DEVICES = "deeco.sim.devices";
	public static final String GOSSIP_DEVICES_DEFAULT = "";
	
	public static final String SIMULATION_DURATION = "deeco.sim.duration";
	public static final Long SIMULATION_DURATION_DEFAULT = (long) 60000;
	
	public static final String SIMULATION_SIMULATORS = "deeco.sim.simulators";
	public static final String SIMULATION_SIMULATORS_DEFAULT = "omnet;matsim";
	
	public static final String SELECTOR_TYPE = "deeco.sendKN.selector";
	public static final String SELECTOR_TYPE_DEFAULT = "static";
	
	public static final String SELECTOR_RANGE_FROM = "deeco.sendKN.range.from";
	public static final String SELECTOR_RANGE_TO = "deeco.sendKN.range.to";
	public static final String PUBLISH_COUNT = "deeco.sendKN.count";
	
	public static final String GROUPER_COUNT = "deeco.grouper.count";
	public static final int GROUPER_COUNT_DEFAULT = 0;
	
	private static int groupers;
	private static List<IPAddress> grouperAddresses = new ArrayList<IPAddress>();
	
	public static DEECoSimulation createSimulation(String configFile) {
		Locale.setDefault(Locale.getDefault());	// ???
		ConfigHelper.loadProperties(configFile);
		
		DEECoSimulation sim = null;
		
		String simulators = System.getProperty(SIMULATION_SIMULATORS, SIMULATION_SIMULATORS_DEFAULT);
		if (simulators.contains("omnet") && simulators.contains("matsim")) {
			OMNeTSimulation omnet = new OMNeTSimulation();
			MatsimPlugin matsim = new MatsimPlugin();
			MatsomnPlugin matsomn = new MatsomnPlugin(matsim.getTimer(), omnet.getTimer());
			sim = new DEECoSimulation(matsomn.getTimer());
			
			sim.addPlugin(matsim);
			sim.addPlugin(omnet);
			sim.addPlugin(matsomn);
		}
		else if (simulators.contains("omnet")) {
			OMNeTSimulation omnet = new OMNeTSimulation();
			sim = new DEECoSimulation(omnet.getTimer());
			
			sim.addPlugin(omnet);
		}
		else {
			SimulationTimer deeco = new DiscreteEventTimer();
			sim = new DEECoSimulation(deeco);
		}
		
		configureSimulation(sim);
		
		return sim;
	}
	
	private static void configureSimulation(DEECoSimulation sim) {
		
		String features = System.getProperty(GOSSIP_FEATURES, GOSSIP_FEATURES_DEFAULT);
		
		sim.addPlugin(Network.class);
		sim.addPlugin(ReceptionBuffer.class);
		sim.addPlugin(KnowledgeProviderPlugin.class);
		sim.addPlugin(AddressRegisterPlugin.class);
		sim.addPlugin(EnsembleDeployerPlugin.class);
		
		if (features.contains("push")) {
			sim.addPlugin(SendKNPlugin.class);
			RequestLoggerPlugin.registerStatParam("ComponentId", new ComponentIdGetter());
			RequestLoggerPlugin.registerStatParam("KNPeriod", new StaticGetter(SendKNPlugin.TASK_PERIOD));
			RequestLoggerPlugin.registerStatParam("IsSource", new SourceGetter());
			sim.addPlugin(ReceiveKNPlugin.class);
		}
		
		if (features.contains("pull")) {
			//sim.addPlugin(SendPulledKNPlugin.class);
			sim.addPlugin(SendHDPlugin.class);
			RequestLoggerPlugin.registerStatParam("HDPeriod", new StaticGetter(SendHDPlugin.TASK_PERIOD));
			sim.addPlugin(SendPLPlugin.class);
			RequestLoggerPlugin.registerStatParam("PLPeriod", new StaticGetter(SendPLPlugin.TASK_PERIOD));
			
			RequestLoggerPlugin.registerStatParam("LocalTimeout", new StaticGetter(ReceptionBuffer.LOCAL_TIMEOUT));
			RequestLoggerPlugin.registerStatParam("GlobalTimeout", new StaticGetter(ReceptionBuffer.GLOBAL_TIMEOUT));
			
			sim.addPlugin(ReceiveHDPlugin.class);
			sim.addPlugin(ReceivePLPlugin.class);
		}
		
		if (features.contains("grouper")) {
			groupers = Integer.getInteger(GROUPER_COUNT, GROUPER_COUNT_DEFAULT);
			rangeConfig = new DefaultRangeConfigurator(groupers);
			RequestLoggerPlugin.registerStatParam("Groupers", new StaticGetter(GROUPER_COUNT));
			
			EnsembleDeployerPlugin.registerPreprocessor(new PartitionedByProcessor());
		}
		
		if (features.contains("push") || features.contains("pull")) {
			sim.addPlugin(GossipRebroadcastPlugin.class);
			
			RequestLoggerPlugin.registerStatParam("Probability", new StaticGetter(GossipRebroadcastPlugin.REBROADCAST_PROBABILITY));
		}
		
		if (features.contains("logger")) {
			sim.addPlugin(RequestLoggerPlugin.class);
			sim.addPlugin(KnowledgeAgeGetter.class);
		}
		
		String devices = System.getProperty(GOSSIP_DEVICES, GOSSIP_DEVICES_DEFAULT);
		if (devices.equalsIgnoreCase("deeco-broadcast")) {
			sim.addPlugin(new BroadcastDevice());
		}
		else if (devices.equalsIgnoreCase("deeco-multicast")) {
			sim.addPlugin(new MulticastDevice());
		}
		else if (devices.equalsIgnoreCase("omnet-broadcast")) {
			sim.addPlugin(OMNeTBroadcastDevice.class);
		}
	}
	public static DEECoNode createNode(DEECoSimulation sim, int nodeId) throws InstantiationException, IllegalAccessException, DEECoException {
		
		DEECoNode node = null;
		
		String devices = System.getProperty(GOSSIP_DEVICES, GOSSIP_DEVICES_DEFAULT);
		ArrayList<DEECoPlugin> plugins = new ArrayList<DEECoPlugin>();
		
		if (devices.equalsIgnoreCase("omnet-infrastructure")) {
			IPAddress address = AddressHelper.createIP(nodeId);
			plugins.add(new OMNeTInfrastructureDevice(address));
		}
		
		String features = System.getProperty(GOSSIP_FEATURES, GOSSIP_FEATURES_DEFAULT);
		if (features.contains("grouper")) {
			if (grouperAddresses.size() < groupers) {
				plugins.add(new GrouperServerPlugin());
				plugins.add(new GrouperAddressRegisterPlugin(rangeConfig));
				grouperAddresses.add(AddressHelper.createIP(nodeId));
			}
			else {
				plugins.add(new GrouperClientPlugin());
			}
		}
		
		DEECoPlugin[] pluginsArray = new DEECoPlugin[plugins.size()];
		plugins.toArray(pluginsArray);
		node = sim.createNode(nodeId, pluginsArray);
		
		configureGossip(node);
		
		return node;
	}

	private static void configureGossip(DEECoNode node) {
		
		RequestLoggerPlugin logger = node.getPluginInstance(RequestLoggerPlugin.class);
		int count = Integer.getInteger(PUBLISH_COUNT, 0);
		logger.registerParam("PublishCount", new StaticGetter(PUBLISH_COUNT));
		
		String features = System.getProperty(GOSSIP_FEATURES, GOSSIP_FEATURES_DEFAULT);
		if (features.contains("grouper")) {
			// initialise with grouper address, in the case of grouper with other grouper address
			AddressRegister register = node.getPluginInstance(AddressRegisterPlugin.class).getRegister();
			IPAddress grouperAddr = AddressHelper.createIP(node.getId() % groupers + 1);
			register.add(grouperAddr);
		} else {
			SendKNPlugin sendKN = node.getPluginInstance(SendKNPlugin.class);
			RecipientSelector selector = sendKN.getRecipientSelector();
			
			int from = Integer.getInteger(SELECTOR_RANGE_FROM, 0);
			int to = Integer.getInteger(SELECTOR_RANGE_TO, 0);
			
			selector = new RangeRecipientSelector(node.getId(), from, to, count);
			sendKN.setRecipientSelector(selector);
		}
	}
	
	public static void runSimulation(DEECoSimulation sim) {
		long duration = Long.getLong(SIMULATION_DURATION, SIMULATION_DURATION_DEFAULT);
		sim.start(duration);
	}
	
	
	
}
