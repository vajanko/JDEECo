/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.demo;

import java.io.FileNotFoundException;
import java.util.Locale;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.timer.DiscreteEventTimer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.gossip.device.MulticastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.GossipRebroadcastStrategy;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class Grid {

	/**
	 * @param args
	 * @throws DEECoException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws FileNotFoundException 
	 * @throws AnnotationProcessorException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, FileNotFoundException, AnnotationProcessorException {
		
		ConfigHelper.loadProperties("test/cz/cuni/mff/d3s/jdeeco/gossip/demo/grid-pull-passive.properties");
		
		//RequestLoggerPlugin.initOutputStream("logs/grid.csv");
		
		int size = 4;
		final int nodes = size * size;
		
		StringBuilder top = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				int n = i * size + j + 1;
				
				if (n + 1 <= (i + 1 ) * size)
					top.append(String.format("(%d,%d)", n, n + 1));
				if (n + size <= nodes)
					top.append(String.format("(%d,%d)", n, n + size));
			}
		}
		
		System.getProperties().setProperty(MulticastDevice.MULTICAST_TOPOLOGY, top.toString());
		
		for (Double prob = 0.1 ; prob <= 1.0; prob += 0.1) {
			
			String probStr = String.format(Locale.getDefault(), "%.2f", prob);
			System.getProperties().setProperty(GossipRebroadcastStrategy.REBROADCAST_PROBABILITY, probStr);
		
			SimulationTimer simulationTimer = new DiscreteEventTimer();
			DEECoSimulation realm = new DEECoSimulation(simulationTimer);
			GossipPlugin.registerPlugin(realm);
			
			for (int i = 1; i <= nodes; ++i) {
				DEECoNode node = realm.createNode(i);
				node.deployComponent(new DemoComponent(i));
				node.deployEnsemble(DemoEnsemble.class);
			}
	
			realm.start(10000);
		}
	}
}
