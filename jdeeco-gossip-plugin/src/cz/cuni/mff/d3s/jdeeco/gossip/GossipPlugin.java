/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFrameworkImpl;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.Task;
import cz.cuni.mff.d3s.jdeeco.gossip.strategy.MessageUpdateStrategy;
import cz.cuni.mff.d3s.jdeeco.gossip.task.PullKnowledgeTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.task.PushKnowledgeTaskListener;
import cz.cuni.mff.d3s.jdeeco.network.Network;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class GossipPlugin implements DEECoPlugin {
	
	private Network network;
	private RuntimeFramework runtime;
	private MessageBuffer messageBuffer = new MessageBuffer();

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
		
		Scheduler scheduler = runtime.getScheduler();
		
		// insert strategy for observing knowledge updates
		MessageUpdateStrategy updateStrategy = new MessageUpdateStrategy(messageBuffer, scheduler.getTimer());
		network.getL2().registerL2Strategy(updateStrategy);
		
		// TODO: load setup parameters such a the probabilities and periods of tasks
		// these parameters can be then changed at runtime by the adaptable protocol
	
		// run PUSH gossip task
		String nodeId = String.valueOf(container.getId());
		PushKnowledgeTaskListener pushListener = new PushKnowledgeTaskListener(nodeId, runtime, network);
		Task publishTask = new CustomStepTask(scheduler, pushListener);
		scheduler.addTask(publishTask);
		
		// TODO: run message headers gossipping task
		
		// run PULL gossip task
		PullKnowledgeTaskListener pullListener = new PullKnowledgeTaskListener(messageBuffer, runtime, network);
		Task pullTask = new CustomStepTask(scheduler, pullListener);
		scheduler.addTask(pullTask);
	}

}
