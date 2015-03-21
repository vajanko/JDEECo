/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.demo;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.timer.DiscreteEventTimer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.PushPullBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.device.BroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveHDStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveKNStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceivePLStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPLPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPulledKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPushedKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class Launcher {
	public static void main(String[] args) throws DEECoException, AnnotationProcessorException, InstantiationException, IllegalAccessException {
		
		GossipProperties.initialize();
		
		/* create main application container */
		SimulationTimer simulationTimer = new DiscreteEventTimer();
		DEECoSimulation realm = new DEECoSimulation(simulationTimer);
		
		realm.addPlugin(Network.class);
		// only one instance for all nodes
		BroadcastDevice broadcast = new BroadcastDevice();
		realm.addPlugin(broadcast);
		
		realm.addPlugin(GossipPlugin.class);
		realm.addPlugin(KnowledgeProvider.class);
		realm.addPlugin(PushPullBuffer.class);
		
		realm.addPlugin(RequestLoggerPlugin.class);
		
		realm.addPlugin(SendPushedKNPlugin.class);
		realm.addPlugin(SendPulledKNPlugin.class);
		realm.addPlugin(SendHDPlugin.class);
		realm.addPlugin(SendPLPlugin.class);
		
		realm.addPlugin(ReceiveKNStrategy.class);
		realm.addPlugin(ReceiveHDStrategy.class);
		realm.addPlugin(ReceivePLStrategy.class);
		
		realm.addPlugin(GossipRebroadcastStrategy.class);

		/* create first deeco node */
		DEECoNode deeco1 = realm.createNode(1);
		/* deploy components and ensembles */
		deeco1.deployComponent(new DemoComponent("D1"));
		deeco1.deployEnsemble(DemoEnsemble.class);

		/* create second deeco node */
		DEECoNode deeco2 = realm.createNode(2);
		/* deploy components and ensembles */
		deeco2.deployComponent(new DemoComponent("D2"));
		deeco2.deployEnsemble(DemoEnsemble.class);

		/* WHEN simulation is performed */
		realm.start(8000);
	}
}
