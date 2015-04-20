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
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class Pulling {
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException, FileNotFoundException {
		ConfigHelper.loadProperties("test/cz/cuni/mff/d3s/jdeeco/gossip/demo/pulling.properties");
		
		//RequestLoggerPlugin.initOutputStream("logs/pulling.csv");
		//RequestLoggerPlugin.initOutputStream(System.out);
		
		for (int i = 0; i <= 10; ++i) {
			
			double prob = 0.1 * i;
			String probStr = String.format(Locale.getDefault(), "%.2f", prob);
			System.getProperties().setProperty(GossipRebroadcastStrategy.REBROADCAST_PROBABILITY, probStr);
			System.getProperties().setProperty(RequestLoggerPlugin.LOGGER_ARG1, probStr);
		
			SimulationTimer simulationTimer = new DiscreteEventTimer();
			DEECoSimulation realm = new DEECoSimulation(simulationTimer);
			GossipPlugin.registerPlugin(realm);
			
			DEECoNode deeco1 = realm.createNode(1);
			DEECoNode deeco2 = realm.createNode(2);
			DEECoNode deeco3 = realm.createNode(3);
			DEECoNode deeco4 = realm.createNode(4);
			
			deeco1.deployComponent(new DemoComponent("D1"));
			deeco1.deployEnsemble(DemoEnsemble.class);
			
			deeco2.deployEnsemble(DemoEnsemble.class);
			deeco3.deployEnsemble(DemoEnsemble.class);
			
			deeco4.deployEnsemble(DemoEnsemble.class);
			deeco4.deployComponent(new DemoComponent("D4"));
	
			realm.start(10000);
		}
	}
}
