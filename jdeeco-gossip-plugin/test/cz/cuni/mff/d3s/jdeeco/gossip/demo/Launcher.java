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
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProviderPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.gossip.device.BroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceiveHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceiveKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceivePLPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendPLPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;

/**
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
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
		realm.addPlugin(KnowledgeProviderPlugin.class);
		realm.addPlugin(ReceptionBuffer.class);
		realm.addPlugin(AddressRegisterPlugin.class);
		
		realm.addPlugin(RequestLoggerPlugin.class);
		
		realm.addPlugin(SendKNPlugin.class);
		realm.addPlugin(SendHDPlugin.class);
		realm.addPlugin(SendPLPlugin.class);
		
		realm.addPlugin(ReceiveKNPlugin.class);
		realm.addPlugin(ReceiveHDPlugin.class);
		realm.addPlugin(ReceivePLPlugin.class);
		
		realm.addPlugin(GossipRebroadcastStrategy.class);

		/* create first deeco node */
		DEECoNode deeco1 = realm.createNode(1);
		/* deploy components and ensembles */
		deeco1.deployComponent(new DemoComponent(1));
		deeco1.deployEnsemble(DemoEnsemble.class);

		/* create second deeco node */
		DEECoNode deeco2 = realm.createNode(2);
		/* deploy components and ensembles */
		deeco2.deployComponent(new DemoComponent(2));
		deeco2.deployEnsemble(DemoEnsemble.class);

		/* WHEN simulation is performed */
		realm.start(8000);
	}
}
