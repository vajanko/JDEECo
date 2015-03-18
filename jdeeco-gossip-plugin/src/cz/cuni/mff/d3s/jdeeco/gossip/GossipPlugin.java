/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.network.KnowledgeDataManager;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.Task;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.GossipRebroadcastStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.MessageUpdateStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.PullKnowledgeTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.task.PushHeadersTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.task.PushKnowledgeTaskListener;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.MarshallerRegistry;
import cz.cuni.mff.d3s.jdeeco.network.marshaller.SerializingMarshaller;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class GossipPlugin implements DEECoPlugin {
	
	private Network network;
	private RuntimeFramework runtime;
	private MessageBuffer messageBuffer;
	private KnowledgeProvider knowledgeProvider; 
	
	public KnowledgeProvider getKnowledgeProvider() {
		return knowledgeProvider;
	}
	public MessageBuffer getMessageBuffer() {
		return messageBuffer;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// plugin dependencies
		this.network = container.getPluginInstance(Network.class);
		this.runtime = container.getRuntimeFramework();
		
		this.messageBuffer = new MessageBuffer();
		this.knowledgeProvider = new KnowledgeProvider();
		this.knowledgeProvider.init(container);
		
		Scheduler scheduler = runtime.getScheduler();
		
		// insert strategy for observing knowledge updates
		Layer2 layer2 = network.getL2();
		MessageUpdateStrategy updateStrategy = new MessageUpdateStrategy(runtime, this);
		layer2.registerL2Strategy(updateStrategy);
		
		GossipRebroadcastStrategy rebroadcastStrategy = new GossipRebroadcastStrategy(runtime, network);
		layer2.registerL2Strategy(rebroadcastStrategy);
		
		// TODO: load setup parameters such a the probabilities and periods of tasks
		// these parameters can be then changed at runtime by the adaptable protocol
	
		// run PUSH knowledge gossip task
		PushKnowledgeTaskListener pushListener = new PushKnowledgeTaskListener(runtime, network, this);
		Task publishTask = new CustomStepTask(scheduler, pushListener);
		scheduler.addTask(publishTask);
		
		// run PUSH message headers gossip task
		PushHeadersTaskListener pushHeadersListener = new PushHeadersTaskListener(runtime, network, this);
		Task pushHeadersTask = new CustomStepTask(scheduler, pushHeadersListener);
		scheduler.addTask(pushHeadersTask);
		
		// run PULL knowledge gossip task
		PullKnowledgeTaskListener pullListener = new PullKnowledgeTaskListener(runtime, network, this);
		Task pullTask = new CustomStepTask(scheduler, pullListener);
		scheduler.addTask(pullTask);
		
		// register marshalers for other types of data except knowledge
		MarshallerRegistry registry = network.getL2().getMarshallers();
		registry.registerMarshaller(L2PacketType.MESSAGE_HEADERS, new SerializingMarshaller());
		registry.registerMarshaller(L2PacketType.PULL_REQUEST, new SerializingMarshaller());
	}

}
