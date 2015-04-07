/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.omnet;

import java.io.IOException;
import java.util.Locale;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.timer.DiscreteEventTimer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.jdeeco.gossip.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class Omnet {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws DEECoException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws AnnotationProcessorException 
	 */
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
		
		// this is wonderful !!!
		Locale.setDefault(Locale.getDefault());
		
		ConfigHelper.loadProperties("test/cz/cuni/mff/d3s/jdeeco/gossip/omnet/omnet.properties");
		
		RequestLoggerPlugin.initOutputStream("logs/omnet.csv");
		
		final int nodes = 3;
		
		for (Double prob = 0.1 ; prob <= 1.0; prob += 0.1) {
			
			String probStr = String.format(Locale.getDefault(), "%.2f", prob);
			System.getProperties().setProperty(GossipRebroadcastStrategy.REBROADCAST_PROBABILITY, probStr);
			System.getProperties().setProperty(RequestLoggerPlugin.LOGGER_ARG1, probStr);
		
			OMNeTSimulation omnet = new OMNeTSimulation();
			
			// Create main application container
			DEECoSimulation sim = new DEECoSimulation(omnet.getTimer());
			GossipPlugin.registerPlugin(sim);
			sim.addPlugin(omnet);
			
			for (int i = 0; i < nodes; ++i) {
				DEECoNode node = sim.createNode(i);
				node.deployComponent(new DemoComponent("D" + String.valueOf(i)));
				node.deployEnsemble(DemoEnsemble.class);
			}
			
			sim.start(10000);
			//System.out.println("\n\n###\n");
		}
		
		
	}

}
