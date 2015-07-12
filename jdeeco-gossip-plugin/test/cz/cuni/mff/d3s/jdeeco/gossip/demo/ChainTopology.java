/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.demo;

import java.util.ArrayList;

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
import cz.cuni.mff.d3s.jdeeco.gossip.device.MulticastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.device.NetworkLink;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.GossipRebroadcastPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceiveHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceiveKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceivePLPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendPLPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ChainTopology {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
		
		GossipProperties.initialize();
		
		/* create main application container */
		SimulationTimer simulationTimer = new DiscreteEventTimer();
		DEECoSimulation realm = new DEECoSimulation(simulationTimer);
		
		realm.addPlugin(Network.class);
		
		realm.addPlugin(GossipPlugin.class);
		realm.addPlugin(KnowledgeProviderPlugin.class);
		realm.addPlugin(ReceptionBuffer.class);
		
		// print to console as well as to the file
		realm.addPlugin(RequestLoggerPlugin.class);
		
		realm.addPlugin(SendKNPlugin.class);
		realm.addPlugin(SendHDPlugin.class);
		realm.addPlugin(SendPLPlugin.class);
		
		realm.addPlugin(ReceiveKNPlugin.class);
		realm.addPlugin(ReceiveHDPlugin.class);
		realm.addPlugin(ReceivePLPlugin.class);
		
		realm.addPlugin(GossipRebroadcastPlugin.class);
		
		// create a following topology
		// 1 --- 3 --- 4 --- 2
		// on node 1 and 2 are deployed components D1 and D2 respectively 
			
		ArrayList<NetworkLink> links = new ArrayList<NetworkLink>();
		links.add(new NetworkLink(1, 3));
		links.add(new NetworkLink(3, 4));
		links.add(new NetworkLink(4, 2));
		MulticastDevice multicast = new MulticastDevice(3, links);
		realm.addPlugin(multicast);
			
		DEECoNode deeco1 = realm.createNode(1);
		DEECoNode deeco3 = realm.createNode(3);
		DEECoNode deeco4 = realm.createNode(4);
		DEECoNode deeco2 = realm.createNode(2);
		
		deeco1.deployComponent(new DemoComponent(1));
		deeco1.deployEnsemble(DemoEnsemble.class);
		
		deeco2.deployComponent(new DemoComponent(2));
		deeco2.deployEnsemble(DemoEnsemble.class);
		
		deeco3.deployEnsemble(DemoEnsemble.class);
		deeco4.deployEnsemble(DemoEnsemble.class);
		//deeco3.deployComponent(new DemoComponent("O3"));
		//deeco4.deployComponent(new DemoComponent("O4"));

		/* WHEN simulation is performed */
		realm.start(10000);
	}
}
