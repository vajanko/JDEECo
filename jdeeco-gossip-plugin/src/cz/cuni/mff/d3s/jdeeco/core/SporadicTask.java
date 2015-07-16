package cz.cuni.mff.d3s.jdeeco.core;

import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;

/**
 * Task with sporadic execution planning by explicitly calling a method.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SporadicTask extends TimerTask {

	/**
	 * Create a new instance of {@link SporadicTask} which will never be executed.
	 * 
	 * @param scheduler Scheduler used to execute current {@link Task} instance.
	 * @param taskListener Listener for time events triggered by a simulation.
	 */
	public SporadicTask(Scheduler scheduler, TimerTaskListener taskListener) {
		// schedule aperiodic with infinite delay
		super(scheduler, taskListener, Long.MAX_VALUE);
	}
	
	/**
	 * Schedule current task for execution after given delay.
	 * 
	 * @param delay Time to wait in milliseconds before current task execution.
	 */
	public void scheduleAt(long delay) {
		this.scheduler.removeTask(this);
		this.trigger.setOffset(delay);
		this.scheduler.addTask(this);
	}

	/**
	 * Schedule current task for immediate execution.
	 */
	public void scheduleNow() {
		scheduleAt(1);
	}
}
