/**
 * 
 */
package cz.cuni.mff.d3s.deeco.measurement;

import java.io.IOException;
import java.util.Locale;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.demo.component.SensorComponent;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
import cz.cuni.mff.d3s.jdeeco.matsomn.MatsomnPlugin;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTBroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimOmnetTest {
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {	
		// this is wonderful !!!
		Locale.setDefault(Locale.getDefault());
		
		String configFile = "config/grid.properties";
		if (args.length > 0)
			configFile = args[0];
		ConfigHelper.loadProperties(configFile);
					
		OMNeTSimulation omnet = new OMNeTSimulation();
		MatsimPlugin matsim = new MatsimPlugin(); 
		MatsomnPlugin matsomn = new MatsomnPlugin(matsim.getTimer(), omnet.getTimer());
		DEECoSimulation sim = new DEECoSimulation(matsomn.getTimer());
		
		sim.addPlugin(matsim);
		sim.addPlugin(omnet);
		sim.addPlugin(matsomn);
		
		sim.addPlugin(OMNeTBroadcastDevice.class);
		// TODO: configure plugins
		GossipPlugin.registerPlugin(sim);
		
		// TODO: create population
		final int nodes = 10;
		for (int i = 1; i <= nodes; ++i) {
			
			AgentSensor sensor = matsim.createAgentSensor(i);
			
			DEECoNode node = sim.createNode(i);
			node.deployComponent(new SensorComponent("D" + String.valueOf(i), sensor));
			node.deployEnsemble(DemoEnsemble.class);
		}
		
		// TODO: simulation duration
		long duration = Long.getLong("deeco.simulation.duration", 60 * 1000);
		sim.start(duration);
	}
}
