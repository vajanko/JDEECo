/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsomn;

import org.matsim.core.config.groups.QSimConfigGroup;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimHelper;

/**
 * Implementation of {@link SimulationTimer} for simulation using OMNeT and MATSim.
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
 */
public class MatsomnTimer implements SimulationTimer {

	private SimulationTimer matsimTimer;
	private SimulationTimer omnetTimer;
	private long simulationStep;
	private long stepCount;
	
	public MatsomnTimer(SimulationTimer matsimTimer, SimulationTimer omnetTimer) {
		this.omnetTimer = omnetTimer;
		this.matsimTimer = matsimTimer;
	}
	
	public void setSimulationStep(long step) {
		this.simulationStep = step;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.Timer#notifyAt(long, cz.cuni.mff.d3s.deeco.timer.TimerEventListener, cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void notifyAt(long time, TimerEventListener listener, DEECoContainer node) {
		this.omnetTimer.notifyAt(time, listener, node);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider#getCurrentMilliseconds()
	 */
	@Override
	public long getCurrentMilliseconds() {
		return omnetTimer.getCurrentMilliseconds();
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.SimulationTimer#start(long)
	 */
	@Override
	public void start(long duration) {
		this.stepCount = duration / simulationStep;
		
		try {
			// run matsim in a different thread
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					matsimTimer.start(duration);
				}
			});
			thread.start();
			
			// wait until matsim is initialised
			synchronized (matsimTimer) {
				matsimTimer.wait();
			}
			
			// run omnet in the current thread
			this.omnetTimer.start(duration);
			
			System.out.println("Omnet finished");
		
			thread.join();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}