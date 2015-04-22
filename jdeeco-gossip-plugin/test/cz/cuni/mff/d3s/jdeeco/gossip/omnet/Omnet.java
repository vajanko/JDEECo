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
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
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
		final double prob = 0.5;
				
		String probStr = String.format(Locale.getDefault(), "%.2f", prob);
		System.getProperties().setProperty(GossipRebroadcastStrategy.REBROADCAST_PROBABILITY, probStr);
		System.getProperties().setProperty(RequestLoggerPlugin.LOGGER_ARG1, probStr);
	
		MatsimPlugin matsim = new MatsimPlugin();
		OMNeTSimulation omnet = new OMNeTSimulation();
		
		DEECoSimulation sim = new DEECoSimulation(matsim.getTimer());
		GossipPlugin.registerPlugin(sim);
		sim.addPlugin(matsim);
		sim.addPlugin(omnet);
		
		for (int i = 0; i < nodes; ++i) {
			DEECoNode node = sim.createNode(i);
			node.deployComponent(new DemoComponent(i));
			node.deployEnsemble(DemoEnsemble.class);
		}
		
		sim.start(10000);
	}

}
