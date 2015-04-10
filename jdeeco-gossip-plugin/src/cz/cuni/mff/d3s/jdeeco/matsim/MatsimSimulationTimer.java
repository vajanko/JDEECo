/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimSimulationTimer implements SimulationTimer, MobsimBeforeSimStepListener, TimerEventListener {

	private Controler controler;
	private TreeSet<SimulationCallback> callbacks = new TreeSet<SimulationCallback>();
	
	private long startTime;
	private long currentTime;
	private long simulationStep;
	
	private List<TimerEventListener> listeners = new ArrayList<TimerEventListener>();
	
	/**
	 * 
	 */
	public MatsimSimulationTimer(Controler controler) {
		this.controler = controler;
		this.controler.getMobsimListeners().add(this);
		this.listeners.add(this);
	}
	
	public void register(TimerEventListener listener) {
		this.listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.TimerEventListener#at(long)
	 */
	@Override
	public void at(long time) {
		while (!callbacks.isEmpty()) {
			// process all callback in this matsim step
			SimulationCallback cb = callbacks.first();
			
			// deeco scheduler is expecting the simulation started at time 00:00
			// if simulation starts at a different time such as 06:00 am the scheduler will
			// execute all periodic events from 00:00 to 06:00
			if (cb.getAbsoluteTime() > time - startTime + simulationStep)
				break;	// this call back will be called in the next simulation step
			
			// remove first
			callbacks.pollFirst();
			
			// execute callback at the time of deeco scheduler
			cb.execute(cb.getAbsoluteTime());
		}
	}

	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.Timer#notifyAt(long, cz.cuni.mff.d3s.deeco.timer.TimerEventListener, cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void notifyAt(long time, TimerEventListener listener, DEECoContainer node) {
		// this method notifies that some callback should be executed at given "time" instant
		this.callbacks.add(new SimulationCallback(node.getId(), time, listener));
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider#getCurrentMilliseconds()
	 */
	@Override
	public long getCurrentMilliseconds() {
		// this fix is necessary because of deeco scheduler which is expecting the simulation
		// to start at time 00:00
		return currentTime - startTime;
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.SimulationTimer#start(long)
	 */
	@Override
	public void start(long duration) {
		// configure end time of the simulation
		QSimConfigGroup group = controler.getConfig().getQSimConfigGroup();
		double startTime = group.getStartTime();
		double endTime = startTime + msTOs(duration);
		group.setEndTime(endTime);
		
		// configure current simulation step length
		this.simulationStep = sTOms(group.getTimeStepSize());
		this.startTime = sTOms(startTime);
		
		// run matsim simulation
		controler.run();
	}
	
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener#notifyMobsimBeforeSimStep(org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void notifyMobsimBeforeSimStep(MobsimBeforeSimStepEvent e) {
		// convert (double) seconds to (long) milliseconds
		this.currentTime = sTOms(e.getSimulationTime()); 
		// fire callbacks which should be executed at "currentTime" as well as any
		// registered listeners
		for (TimerEventListener listener : this.listeners)
			listener.at(currentTime);
		//this.at(currentTime);
	}
	
	private static long sTOms(double s) {
		return Math.round(s * 1000);
	}
	private static double msTOs(long ms) {
		return ((double)ms) / 1000;
	}
}
