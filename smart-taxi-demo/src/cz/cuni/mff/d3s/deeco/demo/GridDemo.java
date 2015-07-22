package cz.cuni.mff.d3s.deeco.demo;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.sim.SimConfig;

public class GridDemo {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
		String configFile = "./config/grid.properties";
		if (args.length > 0)
			configFile = args[0];
		
		DEECoSimulation sim = SimConfig.createSimulation(configFile);

		// this is encoded IP address
		int nodeId = AddressHelper.encodeIP(0, 0, 0, 0);
		final int nodes = 9;
		
		for (int i = 1; i <= nodes; ++i) {
			nodeId++;
			DEECoNode node = SimConfig.createNode(sim, nodeId);
			node.deployComponent(new DemoComponent(nodeId));
		}
		
		SimConfig.runSimulation(sim);
	}

}
