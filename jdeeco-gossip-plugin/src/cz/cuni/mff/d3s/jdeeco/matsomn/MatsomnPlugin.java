/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsomn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

import org.matsim.api.core.v01.Id;
import org.matsim.core.controler.Controler;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNeTNative;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.jdeeco.core.Position;
import cz.cuni.mff.d3s.jdeeco.core.task.PeriodicTask;
import cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimHelper;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimOutput;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * Simulation plugin providing functionality of MATSim as well as OMNeT.
 * Plugin name comes from (MATS)im and (OMN)eT.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsomnPlugin implements DEECoPlugin, TimerTaskListener {
	
	private MatsimPlugin matsim;
	//private OMNeTSimulation omnet;
	// synchronises data between matsim and omnet simulations
	private Exchanger<Object> exchanger;
	// custom simulation timer for hybrid simulation with two threads
	private MatsomnTimer timer;
	// translates matsim coordinates to omnet coordinates
	private MatsomnPositionTranslator translator;
	// 
	private Collection<AgentSensor> matsimSensors = new ArrayList<AgentSensor>();
	// number of MATSim - OMNeT exchanges
	private long stepCount;
	
	/**
	 * 
	 */
	public MatsomnPlugin(SimulationTimer matsimTimer, SimulationTimer omnetTimer) {
		this.timer = new MatsomnTimer(matsimTimer, omnetTimer);
	}

	public SimulationTimer getTimer() {
		return timer;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void at(long time, Object triger) {
		// exchange data between threads
		if (exchanger != null) {
			try {
				Object data = exchanger.exchange(null);//, 5, TimeUnit.SECONDS);
				Map<Id, MatsimOutput> outputs = (Map<Id, MatsimOutput>)data;
				
				// it is important that matsim outputs are updated from this thread
				matsim.getOutputProvider().updateOutputs(outputs);
				
				// update omnet nodes positions
				for (AgentSensor sensor : this.matsimSensors) {
					Position pos = translator.translate(sensor.getPosition());
					OMNeTNative.nativeSetPosition(sensor.getNodeId(), pos.x, pos.y, 0);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#getInitialTask(cz.cuni.mff.d3s.deeco.scheduler.Scheduler)
	 */
	@Override
	public TimerTask getInitialTask(Scheduler scheduler) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(OMNeTSimulation.class, MatsimPlugin.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		if (this.matsim == null) {
			// only initialize once
			this.matsim = container.getPluginInstance(MatsimPlugin.class);
			this.exchanger = new Exchanger<Object>();
			this.matsim.setExchanger(exchanger);
			
			Controler controler = matsim.getControler();
			
			// create translator for matsim coordinate system 
			this.translator = new MatsomnPositionTranslator(controler.getNetwork());
			
			// start periodic exchange task
			double period = controler.getConfig().getQSimConfigGroup().getTimeStepSize();
			Scheduler scheduler = container.getRuntimeFramework().getScheduler();
			scheduler.addTask(new PeriodicTask(scheduler, this, MatsimHelper.sTOms(period)));
			
			this.timer.setSimulationStep(MatsimHelper.sTOms(period));
		}
		
		AgentSensor sensor = matsim.createAgentSensor(container.getId());
		this.matsimSensors.add(sensor);
	}
}
