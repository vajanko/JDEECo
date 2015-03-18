/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.Task;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.MessageUpdateStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.PullKnowledgePlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.PushHeadersPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.task.PushKnowledgePlugin;
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
		return Arrays.asList(PushKnowledgePlugin.class, PushHeadersPlugin.class, PullKnowledgePlugin.class,
				MessageUpdateStrategy.class, GossipRebroadcastStrategy.class);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {	
		Scheduler scheduler = container.getRuntimeFramework().getScheduler();
	
		// run PUSH knowledge gossip task
		PushKnowledgePlugin pushListener = container.getPluginInstance(PushKnowledgePlugin.class); 
		Task publishTask = new CustomStepTask(scheduler, pushListener);
		scheduler.addTask(publishTask);
		
		// run PUSH message headers gossip task
		PushHeadersPlugin pushHeadersListener = container.getPluginInstance(PushHeadersPlugin.class); 
		Task pushHeadersTask = new CustomStepTask(scheduler, pushHeadersListener);
		scheduler.addTask(pushHeadersTask);
		
		// run PULL knowledge gossip task
		PullKnowledgePlugin pullListener = container.getPluginInstance(PullKnowledgePlugin.class); 
		Task pullTask = new CustomStepTask(scheduler, pullListener);
		scheduler.addTask(pullTask);
		
		// TODO: move these
		// register marshalers for other types of data except knowledge
		Network network = container.getPluginInstance(Network.class);
		MarshallerRegistry registry = network.getL2().getMarshallers();
		registry.registerMarshaller(L2PacketType.MESSAGE_HEADERS, new SerializingMarshaller());
		registry.registerMarshaller(L2PacketType.PULL_REQUEST, new SerializingMarshaller());
	}

}
