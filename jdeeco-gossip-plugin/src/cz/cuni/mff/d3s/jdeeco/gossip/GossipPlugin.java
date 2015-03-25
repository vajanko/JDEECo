/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.device.BroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.device.MulticastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveHDStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveKNStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceivePLStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPLPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPulledKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPushedKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.MarshallerRegistry;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.SerializingMarshaller;

/**
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
 */
public class GossipPlugin implements DEECoPlugin {

	public static final String GOSSIP_FEATURES = "deeco.gossip.features";
	public static final String GOSSIP_FEATURES_DEFAULT = "";
	public static final String GOSSIP_DEVICE = "deeco.gossip.devices";
	public static final String GOSSIP_DEVICE_DEFAULT = "broadcast";
	
	
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
		// TODO: move these
		// register marshalers for other types of data except knowledge
		Network network = container.getPluginInstance(Network.class);
		MarshallerRegistry registry = network.getL2().getMarshallers();
		registry.registerMarshaller(L2PacketType.MESSAGE_HEADERS, new SerializingMarshaller());
		registry.registerMarshaller(L2PacketType.PULL_REQUEST, new SerializingMarshaller());
	}

	/**
	 * @param realm
	 * @return
	 */
	public static void registerPlugin(DEECoSimulation sim) {
		String features = System.getProperty(GOSSIP_FEATURES, GOSSIP_FEATURES_DEFAULT);
		
		sim.addPlugin(Network.class);
		sim.addPlugin(ReceptionBuffer.class);
		sim.addPlugin(KnowledgeProvider.class);
		
		if (features.contains("logger")) {
			sim.addPlugin(RequestLoggerPlugin.class);
		}
		
		if (features.contains("push")) {
			sim.addPlugin(SendPushedKNPlugin.class);
		}
		
		if (features.contains("pull")) {
			sim.addPlugin(SendPulledKNPlugin.class);
			sim.addPlugin(SendHDPlugin.class);
			sim.addPlugin(SendPLPlugin.class);
			
			sim.addPlugin(ReceiveKNStrategy.class);
			sim.addPlugin(ReceiveHDStrategy.class);
			sim.addPlugin(ReceivePLStrategy.class);
		}
		
		if (features.contains("push") || features.contains("pull")) {
			sim.addPlugin(GossipRebroadcastStrategy.class);
		}
		
		String device = System.getProperty(GOSSIP_DEVICE, GOSSIP_DEVICE_DEFAULT);
		if (device.equalsIgnoreCase("broadcast")) {
			sim.addPlugin(new BroadcastDevice());
		}
		else if (device.equalsIgnoreCase("multicast")) {
			sim.addPlugin(new MulticastDevice());
		}
		
		sim.addPlugin(new GossipPlugin());
	}

}
