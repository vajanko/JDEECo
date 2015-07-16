/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;

/**
 * Task with periodic execution. 
 * This task can be dynamically rescheduled with given delay and than automatically
 * continues with its regular periodic execution.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PeriodicTask extends TimerTask {

	/**
	 * Creates a new instance of {@link PeriodicTask} with given period which
	 * starts its execution immediately.
	 * 
	 * @param scheduler Scheduler used to execute current {@link Task} instance.
	 * @param taskListener Listener for time events triggered by a simulation.
	 * @param period Task execution period.
	 */
	public PeriodicTask(Scheduler scheduler, TimerTaskListener taskListener, long period) {
		this(scheduler, taskListener, period, 1);
	}
	
	/**
	 * Creates a new instance of {@link PeriodicTask} with given period which
	 * starts its execution after given delay.
	 * 
	 * @param scheduler Scheduler used to execute current {@link Task} instance.
	 * @param taskListener Listener for time events triggered by a simulation.
	 * @param period Task execution period.
	 * @param delay Initial task delay.
	 */
	public PeriodicTask(Scheduler scheduler, TimerTaskListener taskListener, long period, long delay) {
		super(scheduler, taskListener, delay);
		
		this.trigger.setPeriod(period);
	}
	
	/**
	 * Reschedule current task to be executed after given delay from the
	 * current system or simulation time. After this execution the tasks 
	 * continues with the regular period specified in the constructor.
	 * 
	 * @param delay Time to wait in milliseconds after which current task
	 * should be executed. 
	 */
	public void scheduleAt(long delay) {
		this.scheduler.removeTask(this);
		this.trigger.setOffset(delay);
		this.scheduler.addTask(this);
	}
	/**
	 * Reschedule current task to be executed immediately. After this execution
	 * the tasks continues with the regular period specified in the constructor.
	 */
	public void scheduleNow() {
		scheduleAt(0);
	}

}
