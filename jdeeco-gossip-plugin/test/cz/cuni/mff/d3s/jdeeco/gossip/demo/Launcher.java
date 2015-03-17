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
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.device.BroadcastLoopback;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class Launcher {
	public static void main(String[] args) throws DEECoException, AnnotationProcessorException {
		/* create main application container */
		SimulationTimer simulationTimer = new DiscreteEventTimer();
		DEECoSimulation realm = new DEECoSimulation(simulationTimer);

		BroadcastLoopback loopback = new BroadcastLoopback();

		/* create first deeco node */
		DEECoNode deeco1 = realm.createNode(new Network(), loopback, new GossipPlugin());
		/* deploy components and ensembles */
		deeco1.deployComponent(new DemoComponent("D1"));
		deeco1.deployEnsemble(DemoEnsemble.class);

		/* create second deeco node */
		DEECoNode deeco2 = realm.createNode(new Network(), loopback, new GossipPlugin());
		/* deploy components and ensembles */
		deeco2.deployComponent(new DemoComponent("D2"));
		deeco2.deployEnsemble(DemoEnsemble.class);

		/* WHEN simulation is performed */
		realm.start(8000);
	}
}
