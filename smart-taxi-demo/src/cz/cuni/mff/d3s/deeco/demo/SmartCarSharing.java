package cz.cuni.mff.d3s.deeco.demo;

import java.io.IOException;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.demo.component.DriverComponent;
import cz.cuni.mff.d3s.deeco.demo.component.PassengerComponent;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;
import cz.cuni.mff.d3s.jdeeco.sim.SimConfig;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SmartCarSharing {

	private static int lastId = 0;
	public static int createId() {
		return lastId++;
	}
	
	/**
	 * @param args
	 * @throws DEECoException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws AnnotationProcessorException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException, IOException {	
		String configFile = "./config/test.properties";
		configFile = "C:/tmp/logs/config58.properties";
		if (args.length > 0)
			configFile = args[0];
		
		DEECoSimulation sim = SimConfig.createSimulation(configFile);

		// this is encoded IP address
		int nodeId = AddressHelper.encodeIP(0, 0, 0, 0);
		
		final int drivers = 100;
		final int pedestrians = 100;
		
		for (int i = 1; i <= drivers; ++i) {
			nodeId++;
			DEECoNode node = SimConfig.createNode(sim, nodeId); 
			
			MatsimPlugin matsim = node.getPluginInstance(MatsimPlugin.class);
			String agentId = String.format("pt_%d_1", i);
			AgentSensor sensor = matsim.createAgentSensor(nodeId, agentId);
			
			DriverComponent driver = new DriverComponent(nodeId, sensor);
			node.deployComponent(driver);
		}
		for (int i = 1; i <= pedestrians; ++i) {
			nodeId++;
			DEECoNode node = SimConfig.createNode(sim, nodeId); 

			MatsimPlugin matsim = node.getPluginInstance(MatsimPlugin.class);
			String agentId = String.valueOf(i);
			AgentSensor sensor = matsim.createAgentSensor(nodeId, agentId);
			
			PassengerComponent passenger = new PassengerComponent(nodeId, sensor);
			node.deployComponent(passenger);
		}
		
		SimConfig.runSimulation(sim);
		int i = 0;
		System.out.println(i);
	}

}
