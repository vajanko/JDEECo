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
import cz.cuni.mff.d3s.jdeeco.gossip.RequestLoggerPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
import cz.cuni.mff.d3s.jdeeco.matsomn.MatsomnPlugin;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTBroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimOmnetTest {
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {	
		// this is wonderful !!!
		Locale.setDefault(Locale.getDefault());
		
		ConfigHelper.loadProperties("config/generated.properties");
		
		double prob = 0.5;
		if (args.length > 0)
			prob = Double.parseDouble(args[0]);
		if (args.length > 1)
			;//RequestLoggerPlugin.initOutputStream(args[1]);
			
		for (int si = 0; si < 1; ++si) {
			String probStr = String.format(Locale.getDefault(), "%.2f", prob);
			System.getProperties().setProperty(GossipRebroadcastStrategy.REBROADCAST_PROBABILITY, probStr);
			System.getProperties().setProperty(RequestLoggerPlugin.LOGGER_ARG1, probStr);
			
			OMNeTSimulation omnet = new OMNeTSimulation();
			MatsimPlugin matsim = new MatsimPlugin(); 
			MatsomnPlugin matsomn = new MatsomnPlugin(matsim.getTimer(), omnet.getTimer());
			DEECoSimulation sim = new DEECoSimulation(matsomn.getTimer());
			
			sim.addPlugin(matsim);
			sim.addPlugin(omnet);
			sim.addPlugin(matsomn);
			sim.addPlugin(OMNeTBroadcastDevice.class);
			
			GossipPlugin.registerPlugin(sim);
			
			final int nodes = 10;
			for (int i = 1; i <= nodes; ++i) {
				
				AgentSensor sensor = matsim.createAgentSensor(i);
				
				DEECoNode node = sim.createNode(i);
				node.deployComponent(new SensorComponent("D" + String.valueOf(i), sensor));
				node.deployEnsemble(DemoEnsemble.class);
			}
			
			sim.start(20 * 60 * 1000);
		}
	}
}
