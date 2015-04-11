/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.ArrayList;
import java.util.List;
//import java.util.TreeSet;




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
public class MatsimTimer implements SimulationTimer, MobsimBeforeSimStepListener, MobsimInitializedListener /*, TimerEventListener*/ {

	private Controler controler;
	//private TreeSet<SimulationCallback> callbacks = new TreeSet<SimulationCallback>();
	
	private long startTime;
	private long currentTime;
	private long simulationStep;
	private long stepCount;
	
	private List<TimerEventListener> stepListeners = new ArrayList<TimerEventListener>();
	
	/**
	 * 
	 */
	public MatsimTimer(Controler controler) {
		this.controler = controler;
		this.controler.getMobsimListeners().add(this);
		//this.listeners.add(this);
	}
	
	public void registerStepListener(TimerEventListener listener) {
		this.stepListeners.add(listener);
	}
	
//	/* (non-Javadoc)
//	 * @see cz.cuni.mff.d3s.deeco.timer.TimerEventListener#at(long)
//	 */
//	@Override
//	public void at(long time) {
//		while (!callbacks.isEmpty()) {
//			// process all callback in this matsim step
//			SimulationCallback cb = callbacks.first();
//			
//			// deeco scheduler is expecting the simulation started at time 00:00
//			// if simulation starts at a different time such as 06:00 am the scheduler will
//			// execute all periodic events from 00:00 to 06:00
//			if (cb.getAbsoluteTime() > time - startTime + simulationStep)
//				break;	// this call back will be called in the next simulation step
//			
//			// remove first
//			callbacks.pollFirst();
//			
//			// execute callback at the time of deeco scheduler
//			cb.execute(cb.getAbsoluteTime());
//		}
//	}

	
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
		this.simulationStep = MatsimHelper.sTOms(group.getTimeStepSize());
		this.startTime = MatsimHelper.sTOms(startTime);
		this.stepCount = duration / simulationStep;
		
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
		
		// this step counter prevents from blocking on the exchanger with other thread
		if (this.stepCount <= 0)
			return;
		this.stepCount--;
		
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
