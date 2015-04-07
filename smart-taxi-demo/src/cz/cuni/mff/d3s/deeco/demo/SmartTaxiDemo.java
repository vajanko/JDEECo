package cz.cuni.mff.d3s.deeco.demo;

import java.io.FileNotFoundException;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.gossip.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SmartTaxiDemo {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws DEECoException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws AnnotationProcessorException 
	 */
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
		
		ConfigHelper.loadProperties("config/gossip.properties");
		
		OMNeTSimulation omnet = new OMNeTSimulation();
		
		// Create main application container
		DEECoSimulation sim = new DEECoSimulation(omnet.getTimer());
		GossipPlugin.registerPlugin(sim);
		sim.addPlugin(omnet);
		
		final int nodes = 3;
		for (int i = 0; i < nodes; ++i) {
			DEECoNode node = sim.createNode(i);
			node.deployComponent(new DemoComponent("D" + String.valueOf(i)));
			node.deployEnsemble(DemoEnsemble.class);
		}
		
		sim.start(10000);
	}

}
