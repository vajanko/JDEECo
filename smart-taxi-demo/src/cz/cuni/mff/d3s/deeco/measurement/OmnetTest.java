/**
 * 
 */
package cz.cuni.mff.d3s.deeco.measurement;

import java.util.Locale;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.demo.component.DriverComponent;
import cz.cuni.mff.d3s.deeco.demo.component.PassengerComponent;
import cz.cuni.mff.d3s.deeco.demo.ensemble.BoundaryPositionAggregator;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTBroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;
import cz.cuni.mff.d3s.jdeeco.sim.DummySensor;

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
		
		ConfigHelper.loadProperties("config/berlin.properties");
		
		double prob = 0.0;

		for (int si = 0; si < 1; ++si) {
//			String probStr = String.format(Locale.getDefault(), "%.2f", prob);
//			System.getProperties().setProperty(GossipRebroadcastStrategy.REBROADCAST_PROBABILITY, probStr);
//			System.getProperties().setProperty(RequestLoggerPlugin.LOGGER_ARG1, probStr);
			
			OMNeTSimulation omnet = new OMNeTSimulation();
			DEECoSimulation sim = new DEECoSimulation(omnet.getTimer());
			sim.addPlugin(OMNeTBroadcastDevice.class);
			sim.addPlugin(omnet);
			
			GossipPlugin.registerPlugin(sim);
			
			// this is encoded IP address
			int nodeId = 0;
			
			final int drivers = 10;
			for (int i = 1; i <= drivers; ++i) {
				nodeId++;
				AgentSensor sensor = new DummySensor(nodeId, 100 * i, 100, omnet.getTimer()); 
				
				DEECoNode node = sim.createNode(nodeId);
				
				DriverComponent driver = new DriverComponent(nodeId, sensor);
				node.deployComponent(driver);
				node.deployEnsemble(BoundaryPositionAggregator.class);
			}
			
			final int pedestrians = 30;
			for (int i = 1; i <= pedestrians; ++i) {
				nodeId++;
				AgentSensor sensor = new DummySensor(nodeId, 100 * i, 100, omnet.getTimer());
				
				DEECoNode node = sim.createNode(nodeId);
				
				PassengerComponent passenger = new PassengerComponent(nodeId, sensor);
				node.deployComponent(passenger);
				node.deployEnsemble(BoundaryPositionAggregator.class);
			}
			
			long duration = Long.getLong("deeco.simulation.duration", 60 * 1000);
			sim.start(duration);
		}
	}

}
