package cz.cuni.mff.d3s.deeco.demo;

import java.io.IOException;
import java.util.Locale;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.demo.component.DriverComponent;
import cz.cuni.mff.d3s.deeco.demo.component.PassengerComponent;
import cz.cuni.mff.d3s.deeco.demo.component.SensorComponent;
import cz.cuni.mff.d3s.deeco.demo.ensemble.PositionAggregator;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
import cz.cuni.mff.d3s.jdeeco.matsomn.MatsomnPlugin;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTBroadcastDevice;
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
		// this is wonderful !!!
		Locale.setDefault(Locale.getDefault());
		ConfigHelper.loadProperties("config/demo.properties");
		
		OMNeTSimulation omnet = new OMNeTSimulation();
		MatsimPlugin matsim = new MatsimPlugin();
		MatsomnPlugin matsomn = new MatsomnPlugin(matsim.getTimer(), omnet.getTimer());
		DEECoSimulation sim = new DEECoSimulation(matsomn.getTimer());
		
		sim.addPlugin(matsim);
		sim.addPlugin(omnet);
		sim.addPlugin(matsomn);
		sim.addPlugin(OMNeTBroadcastDevice.class);
		
		GossipPlugin.registerPlugin(sim);
		
		int nodeId = 0;
		
		final int drivers = 3;
		for (int i = 1; i <= drivers; ++i) {
			nodeId++;
			String agentId = String.format("pt_%d_1", i);
			AgentSensor sensor = matsim.createAgentSensor(nodeId, agentId);
			
			DEECoNode node = sim.createNode(nodeId);
			
			DriverComponent driver = new DriverComponent("D" + i, sensor);
			node.deployComponent(driver);
			node.deployEnsemble(PositionAggregator.class);
		}
		
		final int pedestrians = 3;
		for (int i = 1; i <= pedestrians; ++i) {
			nodeId++;
			String agentId = String.valueOf(i);
			AgentSensor sensor = matsim.createAgentSensor(nodeId, agentId);
			
			DEECoNode node = sim.createNode(nodeId);
			
			PassengerComponent passenger = new PassengerComponent("P" + i, sensor);
			node.deployComponent(passenger);
			//node.deployComponent(new SensorComponent("S" + i, sensor));
			node.deployEnsemble(PositionAggregator.class);
		}
		
		sim.start(20 * 3600 * 1000);
	}

}
