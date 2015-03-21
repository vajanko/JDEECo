/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveHDStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveKNStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceivePLStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPLPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendHDPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.SendPushedKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.MarshallerRegistry;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.SerializingMarshaller;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class GossipPlugin implements DEECoPlugin {

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(
				SendPushedKNPlugin.class, 
				SendHDPlugin.class, 
				SendPLPlugin.class,
				
				ReceiveKNStrategy.class, 
				ReceiveHDStrategy.class,
				ReceivePLStrategy.class,
				
				GossipRebroadcastStrategy.class);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// TODO: move these
		// register marshalers for other types of data except knowledge
		Network network = container.getPluginInstance(Network.class);
		MarshallerRegistry registry = network.getL2().getMarshallers();
		registry.registerMarshaller(L2PacketType.MESSAGE_HEADERS, new SerializingMarshaller());
		registry.registerMarshaller(L2PacketType.PULL_REQUEST, new SerializingMarshaller());
	}

}
