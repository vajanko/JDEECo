package cz.cuni.mff.d3s.deeco.demo;

import java.io.File;
import java.io.IOException;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.demo.component.PedestrianComponent;
import cz.cuni.mff.d3s.deeco.demo.ensemble.PositionAggregator;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.matsim.MATSimSimulation;
import cz.cuni.mff.d3s.jdeeco.matsim.MATSimVehicle;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SmartTaxiDemo {

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
		
		ConfigHelper.loadProperties("config/gossip.properties");
		
		OMNeTSimulation omnet = new OMNeTSimulation();
		MATSimSimulation matsim = new MATSimSimulation(new File("config/matsim/config2.xml"));
		
		// Create main application container
		DEECoSimulation sim = new DEECoSimulation(matsim.getTimer());
		
		GossipPlugin.registerPlugin(sim);
		//sim.addPlugin(omnet);
		sim.addPlugin(matsim);
		
		/*final int drivers = 1;
		for (int i = 0; i < drivers; ++i) {
			
			int id = createId();
			MATSimVehicle agent = new MATSimVehicle(0, 0); 	// MATSim agent with start position
			DEECoNode node = sim.createNode(id, agent);		// DEECO node with Id and agent as plug-in
			
			DriverComponent driver = new DriverComponent("Driver" + i, agent); // DEECO component controlling the vehicle
			node.deployComponent(driver);
			node.deployEnsemble(PositionAggregator.class);
			
			
		}*/
		
		final int pedestrians = 3;
		for (int i = 0; i < pedestrians; ++i) {
			
			int id = createId();
			MATSimVehicle agent = new MATSimVehicle(-25000, 0); 	// MATSim agent with start position
			DEECoNode node = sim.createNode(id, agent);		// DEECO node with Id and agent as plug-in
			
			PedestrianComponent pedestrian = new PedestrianComponent("Pedestrian" + i, agent); // DEECO component controlling the vehicle
			node.deployComponent(pedestrian);
			node.deployEnsemble(PositionAggregator.class);
		}
		
		sim.start(20 * 3600 * 1000);
	}

}
