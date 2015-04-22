/**
 * 
 */
package cz.cuni.mff.d3s.deeco.measurement;

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
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTBroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class OmnetTest {

	/**
	 * @param args
	 * @throws DEECoException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws AnnotationProcessorException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
		// this is wonderful !!!
		Locale.setDefault(Locale.getDefault());
		
		ConfigHelper.loadProperties("config/test.properties");
		
		double prob = 0.5;

		for (int si = 0; si < 2; ++si) {
			String probStr = String.format(Locale.getDefault(), "%.2f", prob);
			System.getProperties().setProperty(GossipRebroadcastStrategy.REBROADCAST_PROBABILITY, probStr);
			System.getProperties().setProperty(RequestLoggerPlugin.LOGGER_ARG1, probStr);
			
			OMNeTSimulation omnet = new OMNeTSimulation();
			DEECoSimulation sim = new DEECoSimulation(omnet.getTimer());
			sim.addPlugin(OMNeTBroadcastDevice.class);
			sim.addPlugin(omnet);
			
			GossipPlugin.registerPlugin(sim);
			
			final int nodes = 3;
			for (int i = 0; i < nodes; ++i) {				
				DEECoNode node = sim.createNode(i);
				node.deployComponent(new DemoComponent("D" + i));
				node.deployEnsemble(DemoEnsemble.class);
			}
			
			sim.start(10 * 1000);
		}
	}

}
