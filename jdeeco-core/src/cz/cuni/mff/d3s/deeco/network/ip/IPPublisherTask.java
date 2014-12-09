/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import cz.cuni.mff.d3s.deeco.DeecoProperties;
import cz.cuni.mff.d3s.deeco.model.runtime.api.TimeTrigger;
import cz.cuni.mff.d3s.deeco.model.runtime.api.Trigger;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.TimeTriggerExt;
import cz.cuni.mff.d3s.deeco.network.PublisherTask;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.Task;
import cz.cuni.mff.d3s.deeco.task.TaskInvocationException;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPPublisherTask extends Task {

	public static final int DEFAULT_PUBLISHING_PERIOD = 3000;
	
	private final TimeTrigger trigger;
	
	/**
	 * @param scheduler
	 */
	public IPPublisherTask(Scheduler scheduler) {
		super(scheduler);
		
		this.trigger = new TimeTriggerExt();
		// this value could be configurable
		this.trigger.setPeriod(DEFAULT_PUBLISHING_PERIOD);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.Task#invoke(cz.cuni.mff.d3s.deeco.model.runtime.api.Trigger)
	 */
	@Override
	public void invoke(Trigger trigger) throws TaskInvocationException {
		// TODO Auto-generated method stub
		System.out.println("Publishing IP data");
		
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.Task#registerTriggers()
	 */
	@Override
	protected void registerTriggers() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.Task#unregisterTriggers()
	 */
	@Override
	protected void unregisterTriggers() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.Task#getTimeTrigger()
	 */
	@Override
	public TimeTrigger getTimeTrigger() {
		return trigger;
	}

}
