/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.demo;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

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
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.gossip.device.MulticastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.device.NetworkLink;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.KnowledgeReceptionStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveHDStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveKNStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceivePLStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPLPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPushedKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ChainTopology {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException, FileNotFoundException {
		
		GossipProperties.initialize();
		
		/* create main application container */
		SimulationTimer simulationTimer = new DiscreteEventTimer();
		DEECoSimulation realm = new DEECoSimulation(simulationTimer);
		
		realm.addPlugin(Network.class);
		
		realm.addPlugin(GossipPlugin.class);
		realm.addPlugin(KnowledgeProvider.class);
		realm.addPlugin(ReceptionBuffer.class);
		
		// print to console as well as to the file
		realm.addPlugin(RequestLoggerPlugin.class);
		
		realm.addPlugin(SendPushedKNPlugin.class);
		//realm.addPlugin(SendPulledKNPlugin.class);
		realm.addPlugin(SendHDPlugin.class);
		realm.addPlugin(SendPLPlugin.class);
		
		realm.addPlugin(ReceiveKNStrategy.class);
		realm.addPlugin(ReceiveHDStrategy.class);
		realm.addPlugin(ReceivePLStrategy.class);
		
		realm.addPlugin(GossipRebroadcastStrategy.class);
		realm.addPlugin(KnowledgeReceptionStrategy.class);
		
		// create a following topology
		// 1 --- 3 --- 4 --- 2
		// on node 1 and 2 are deployed components D1 and D2 respectively 
			
		ArrayList<NetworkLink> links = new ArrayList<NetworkLink>();
		links.add(new NetworkLink(1, 3));
		links.add(new NetworkLink(3, 4));
		links.add(new NetworkLink(4, 2));
		MulticastDevice multicast = new MulticastDevice(3, links);
		realm.addPlugin(multicast);
		
		PrintStream logStream = new PrintStream("chain.csv");
		logStream.println("Node;Time;Action;Type;Data");
		RequestLoggerPlugin logPlugin = new RequestLoggerPlugin(logStream);
		
		DEECoNode deeco1 = realm.createNode(1, logPlugin);
		DEECoNode deeco3 = realm.createNode(3, logPlugin);
		DEECoNode deeco4 = realm.createNode(4, logPlugin);
		DEECoNode deeco2 = realm.createNode(2, logPlugin);
		
		deeco1.deployComponent(new DemoComponent("D1"));
		deeco1.deployEnsemble(DemoEnsemble.class);
		
		deeco2.deployComponent(new DemoComponent("D2"));
		deeco2.deployEnsemble(DemoEnsemble.class);
		
		deeco3.deployEnsemble(DemoEnsemble.class);
		deeco4.deployEnsemble(DemoEnsemble.class);
		//deeco3.deployComponent(new DemoComponent("O3"));
		//deeco4.deployComponent(new DemoComponent("O4"));

		/* WHEN simulation is performed */
		realm.start(10000);
		
		logStream.close();
	}
}
