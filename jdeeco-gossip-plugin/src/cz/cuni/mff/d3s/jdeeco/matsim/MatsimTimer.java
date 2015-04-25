/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.ArrayList;
import java.util.List;

import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;
import org.matsim.core.mobsim.framework.events.MobsimInitializedEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener;
import org.matsim.core.mobsim.framework.listeners.MobsimInitializedListener;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimTimer implements SimulationTimer, MobsimBeforeSimStepListener, MobsimInitializedListener {

	private Controler controler;
	
	private long startTime;
	private long currentTime;
	
	private List<TimerEventListener> stepListeners = new ArrayList<TimerEventListener>();
	
	/**
	 * 
	 */
	public MatsimTimer(Controler controler) {
		this.controler = controler;
		this.controler.getMobsimListeners().add(this);
	}
	
	public void registerStepListener(TimerEventListener listener) {
		this.stepListeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.Timer#notifyAt(long, cz.cuni.mff.d3s.deeco.timer.TimerEventListener, cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void notifyAt(long time, TimerEventListener listener, DEECoContainer node) {
		// this method notifies that some callback should be executed at given "time" instant
		//this.callbacks.add(new SimulationCallback(node.getId(), time, listener));
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
		double endTime = startTime + MatsimHelper.msTOs(duration);
		group.setEndTime(endTime);
		
		// configure current simulation step length
		this.startTime = MatsimHelper.sTOms(startTime);
		
		// run matsim simulation
		controler.run();
		System.out.println("Matsim finished");
	}	
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener#notifyMobsimBeforeSimStep(org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void notifyMobsimBeforeSimStep(MobsimBeforeSimStepEvent e) {	
		// convert (double) seconds to (long) milliseconds
		this.currentTime = MatsimHelper.sTOms(e.getSimulationTime());
		
		// fire callbacks which should be executed at "currentTime" as well as any
		// registered listeners
		for (TimerEventListener listener : this.stepListeners)
			listener.at(currentTime);
	}

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.listeners.MobsimInitializedListener#notifyMobsimInitialized(org.matsim.core.mobsim.framework.events.MobsimInitializedEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void notifyMobsimInitialized(MobsimInitializedEvent e) {	
		synchronized (this) {
			this.notify();
		}
	}
}
