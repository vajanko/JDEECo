package cz.cuni.mff.d3s.jdeeco.gossip.send;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeProviderPlugin;
import cz.cuni.mff.d3s.jdeeco.core.PeriodicTask;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;

/**
 * Base class for gossip send tasks working with message buffer.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public abstract class SendBasePlugin implements TimerTaskListener, DEECoPlugin {
	
	protected KnowledgeProviderPlugin knowledgeProvider;
	protected ReceptionBuffer receptionBuffer;
	protected Layer2 networkLayer;
	
	private long period;
	private long delay = 0;
	
	/**
	 * 
	 */
	public SendBasePlugin(long period) {
		this.period = period;
		this.delay = GossipHelper.generator.nextInt((int)period);
	}
		
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public abstract void at(long time, Object triger);
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#getInitialTask(cz.cuni.mff.d3s.deeco.scheduler.Scheduler)
	 */
	@Override
	public TimerTask getInitialTask(Scheduler scheduler) {
		return null;	// this method is unused
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, ReceptionBuffer.class, KnowledgeProviderPlugin.class, AddressRegisterPlugin.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		this.receptionBuffer = container.getPluginInstance(ReceptionBuffer.class);
		this.knowledgeProvider = container.getPluginInstance(KnowledgeProviderPlugin.class);
		
		// run PULL knowledge gossip task
		Scheduler scheduler = container.getRuntimeFramework().getScheduler();
		scheduler.addTask(new PeriodicTask(scheduler, this, period, delay));
	}
}
