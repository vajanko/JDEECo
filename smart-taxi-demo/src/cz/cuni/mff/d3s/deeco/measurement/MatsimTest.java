/**
 * 
 */
package cz.cuni.mff.d3s.deeco.measurement;

import java.util.Locale;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.demo.component.SensorComponent;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimTest {

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
		ConfigHelper.loadProperties("config/generated.properties");
		
		MatsimPlugin matsim = new MatsimPlugin();
		DEECoSimulation sim = new DEECoSimulation(matsim.getTimer());
		
		sim.addPlugin(matsim);
		
		final int nodes = 1;
		for (int i = 1; i <= nodes; ++i) {
						
			AgentSensor sensor = matsim.createAgentSensor(i);
			
			DEECoNode node = sim.createNode(i);
			node.deployComponent(new SensorComponent("D" + String.valueOf(i), sensor));
			node.deployEnsemble(DemoEnsemble.class);
		}
		
		sim.start(20 * 60 * 1000);
	}

}
