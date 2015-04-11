/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo;

import org.matsim.api.core.v01.Id;
import org.matsim.core.basic.v01.IdImpl;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.demo.component.SensorComponent;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimAgentPlugin;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class Test {
	
	/**
	 * @param args
	 * @throws DEECoException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws AnnotationProcessorException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
		
		ConfigHelper.loadProperties("config/gossip.properties");
		
		MatsimPlugin matsim = new MatsimPlugin();
		OMNeTSimulation omnet = new OMNeTSimulation();
		
		DEECoSimulation sim = new DEECoSimulation(matsim.getTimer());
		GossipPlugin.registerPlugin(sim);
		sim.addPlugin(matsim);
		sim.addPlugin(omnet);
			
		final int vehicles = 1;
		for (int i = 0; i < vehicles; ++i) {
			
			Id id = new IdImpl(i + 1);
			
			MatsimAgentPlugin agent = new MatsimAgentPlugin(id);			// MATSim agent with associated person
			DEECoNode node = sim.createNode(i, agent);		// DEECO node with Id and agent as plug-in
			
			SensorComponent sensor = new SensorComponent("Agent" + i, agent.getSensor()); // DEECO component controlling the agent
			node.deployComponent(sensor);
		}
		
		sim.start(10 * 60 * 1000);
	}

}
