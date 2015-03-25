/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveHDStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveKNStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceivePLStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPLPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPulledKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPushedKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class GossipHelper {
	
	private static void initPlugins(DEECoSimulation sim, Collection<String> features) {
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
	}
	
	public static void initialize(DEECoSimulation sim, String config) {
		GossipProperties.initialize(config);
		
		Collection<String> features = GossipProperties.getGossipFeatures();
		initPlugins(sim, features);
	}
	public static void initialize(DEECoSimulation sim, Collection<String> features) {
		initPlugins(sim, features);
	}
}
