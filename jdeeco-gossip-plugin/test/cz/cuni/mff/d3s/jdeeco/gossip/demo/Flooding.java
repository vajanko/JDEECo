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
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.gossip.device.MulticastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.device.NetworkLink;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPushedKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;

/**
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
 */
public class Flooding {

	/**
	 * @param args
	 * @throws DEECoException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws AnnotationProcessorException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException, FileNotFoundException {
		GossipProperties.initialize("test/cz/cuni/mff/d3s/jdeeco/gossip/demo/Flooding.properties");
		
		SimulationTimer simulationTimer = new DiscreteEventTimer();
		DEECoSimulation realm = new DEECoSimulation(simulationTimer);
		
		//realm.addPlugin(RequestLoggerPlugin.class);
		realm.addPlugin(Network.class);
		
		realm.addPlugin(ReceptionBuffer.class);
		realm.addPlugin(KnowledgeProvider.class);
		realm.addPlugin(SendPushedKNPlugin.class);
		
		realm.addPlugin(GossipRebroadcastStrategy.class);
		
		// create a following topology
		// 1 --- 3 --- 4 --- 2
		// on node 1 and 2 are deployed components D1 and D2 respectively 
			
		ArrayList<NetworkLink> links = new ArrayList<NetworkLink>();
		links.add(new NetworkLink(1, 3));
		links.add(new NetworkLink(3, 4));
		links.add(new NetworkLink(4, 2));
		MulticastDevice multicast = new MulticastDevice(3, links);
		realm.addPlugin(multicast);
		
		PrintStream logStream = new PrintStream("flooding.csv");
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

		realm.start(8000);
	}

}
