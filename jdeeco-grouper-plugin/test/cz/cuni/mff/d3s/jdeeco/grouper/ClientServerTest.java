/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.timer.DiscreteEventTimer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeProviderPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoComponent;
import cz.cuni.mff.d3s.jdeeco.gossip.common.DemoEnsemble;
import cz.cuni.mff.d3s.jdeeco.gossip.device.BroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.gossip.receive.ReceiveKNPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.grouper.client.GrouperClientEnsemble;
import cz.cuni.mff.d3s.jdeeco.grouper.client.GrouperClientPlugin;
import cz.cuni.mff.d3s.jdeeco.grouper.server.GrouperServerPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.device.InfrastructureLoopback;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class ClientServerTest {

	/**
	 * @param args
	 * @throws DEECoException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws AnnotationProcessorException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, DEECoException, AnnotationProcessorException {
		
		SimulationTimer simulationTimer = new DiscreteEventTimer();
		DEECoSimulation sim = new DEECoSimulation(simulationTimer);
		
		sim.addPlugin(Network.class);
		sim.addPlugin(ReceptionBuffer.class);
		sim.addPlugin(KnowledgeProviderPlugin.class);
		sim.addPlugin(AddressRegisterPlugin.class);
		sim.addPlugin(SendKNPlugin.class);
		sim.addPlugin(new BroadcastDevice());
		sim.addPlugin(new InfrastructureLoopback());
		sim.addPlugin(ReceiveKNPlugin.class);
		
		int ip1 = AddressHelper.encodeIP(0, 0, 0, 1);
		DEECoNode node0 = sim.createNode(ip1, new GrouperClientPlugin());
		node0.deployComponent(new DemoComponent(ip1, "Berlin"));
		
		int ip2 = AddressHelper.encodeIP(0, 0, 0, 2);
		DEECoNode node1 = sim.createNode(ip2, new GrouperClientPlugin());
		node1.deployComponent(new DemoComponent(ip2, "Prague"));
		
		int ip3 = AddressHelper.encodeIP(0, 0, 0, 3);
		//DEECoNode node2 = sim.createNode(ip3, new GrouperServerPlugin(DemoEnsemble.class));
		
		sim.start(10000);
		
		KnowledgeManager km;

	}

}
