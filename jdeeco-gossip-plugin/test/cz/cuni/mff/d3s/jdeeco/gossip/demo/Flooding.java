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
import cz.cuni.mff.d3s.jdeeco.gossip.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
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
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {	
		SimulationTimer simulationTimer = new DiscreteEventTimer();
		DEECoSimulation realm = new DEECoSimulation(simulationTimer);
		
		ConfigHelper.loadProperties("test/cz/cuni/mff/d3s/jdeeco/gossip/demo/Flooding.properties");
		GossipPlugin.registerPlugin(realm);
				
		DEECoNode deeco1 = realm.createNode(1);
		DEECoNode deeco3 = realm.createNode(3);
		DEECoNode deeco4 = realm.createNode(4);
		DEECoNode deeco2 = realm.createNode(2);
		
		deeco1.deployComponent(new DemoComponent("D1"));
		deeco1.deployEnsemble(DemoEnsemble.class);
		
		deeco2.deployComponent(new DemoComponent("D2"));
		deeco2.deployEnsemble(DemoEnsemble.class);
		
		deeco3.deployEnsemble(DemoEnsemble.class);
		deeco4.deployEnsemble(DemoEnsemble.class);

		realm.start(8000);
	}

}
