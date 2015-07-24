/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

import org.matsim.api.core.v01.Id;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.framework.events.MobsimBeforeCleanupEvent;
import org.matsim.core.mobsim.framework.events.MobsimInitializedEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeCleanupListener;
import org.matsim.core.mobsim.framework.listeners.MobsimInitializedListener;
import org.matsim.core.mobsim.qsim.QSim;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;
import cz.cuni.mff.d3s.jdeeco.matsomn.SimSignal;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;

/**
 * Plugin enabling MATSim simulation. Use only one instance of this plugin. 
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class MatsimPlugin implements DEECoPlugin, TimerEventListener, MobsimInitializedListener, MobsimBeforeCleanupListener {
	public static final String MATSIM_CONFIG = "deeco.matsim.config";
	/**
	 * Default path to MATSim configuration file
	 */
	public static final String MATSIM_CONFIG_DEFAULT = "config/matsim/config.xml";

	// indicates whether simulation is finished
	private boolean finished;
	private Controler controler;
	private MatsimTimer timer;
	
	private Exchanger<Object> exchanger;
	private MatsimOutputProvider outputs = new MatsimOutputProvider();
	
	private Collection<MobsimAgent> agents = new ArrayList<MobsimAgent>();
	
	private Collection<AgentSensor> matsimSensors = new ArrayList<AgentSensor>();
	
	public MatsimPlugin() {
		// create controller
		String configFile = System.getProperty(MATSIM_CONFIG, MATSIM_CONFIG_DEFAULT);
		this.controler = new PreloadingControler(configFile);
		this.controler.setOverwriteFiles(true);
		
		DeecoQSimFactory mobsimFactory = new DeecoQSimFactory();
		this.controler.addMobsimFactory("qsim", mobsimFactory);
		
		this.controler.getMobsimListeners().add(this);
		
		this.timer = new MatsimTimer(controler);
		this.timer.registerStepListener(this);
	}
	public void setExchanger(Exchanger<Object> exchanger) {
		this.exchanger = exchanger;
	}
	/**
	 * Gets matsim agent ID with associated output data
	 * @return
	 */
	private Map<Id, MatsimOutput> getOutputs() {
		
		HashMap<Id, MatsimOutput> outputs = new HashMap<Id, MatsimOutput>();
		
		for (MobsimAgent agent : this.agents) {
			
			Id destLink = agent.getCurrentLinkId();
			if (agent.getState().equals(MobsimAgent.State.LEG))
				destLink = agent.getDestinationLinkId();
			
			MatsimOutput out = new MatsimOutput(agent.getCurrentLinkId(), destLink, agent.getState());
			outputs.put(agent.getId(), out);
		}
		
		return outputs;
	}
	
	public Controler getControler() {
		return controler;
	}
	public SimulationTimer getTimer() {
		return timer;
	}
	public MatsimOutputProvider getOutputProvider() {
		return outputs;
	}
	public AgentSensor createAgentSensor(int nodeId) {
		MatsimAgentSensor sensor = new MatsimAgentSensor(nodeId, controler.getNetwork(), outputs, timer);
		this.matsimSensors.add(sensor);
		return sensor;
	}
	public AgentSensor createAgentSensor(int nodeId, String agentId) {
		MatsimAgentSensor sensor = new MatsimAgentSensor(nodeId, agentId, controler.getNetwork(), outputs, timer);
		this.matsimSensors.add(sensor);
		return sensor;
	}
	public Collection<AgentSensor> getAgentSensors() {
		return this.matsimSensors;
	}
	
	/**
	 * Start simulation with given duration time in milliseconds.
	 * 
	 * @param duration Simulation duration in milliseconds
	 */
	public void start(long duration) {
		timer.start(duration);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.TimerEventListener#at(long)
	 */
	@Override
	public void at(long time) {
		// executes at each simulation step
		if (this.finished)
			return;	// prevents from blocking on the exchanger
		
		// there are two modes of matsim simulation:
		// 1) single matsim simulation - only one thread, matsim updates its outputs itself
		// 2) multiple threads (together with oment) - matsim exchanges its outputs with the other thread
		if (exchanger != null) {
			try {
				Object outputs = getOutputs();			
				Object inputs = exchanger.exchange(outputs);
				if (this.finished = inputs.equals(SimSignal.KILL))		// received KILL signal from the other thread
					return;
				
				// TODO: process inputs
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else {
			this.outputs.updateOutputs(getOutputs());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.listeners.MobsimInitializedListener#notifyMobsimInitialized(org.matsim.core.mobsim.framework.events.MobsimInitializedEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void notifyMobsimInitialized(MobsimInitializedEvent e) {
		
		QSim simulation = (QSim)e.getQueueSimulation();
		
		// get deeco agents from the simulation
		for (MobsimAgent agent : simulation.getAgents()) {
			if (agent instanceof DeecoAgent) {
				this.agents.add(agent);
			}
		}
		
		// update with initial values
		this.outputs.updateOutputs(getOutputs());
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.listeners.MobsimBeforeCleanupListener#notifyMobsimBeforeCleanup(org.matsim.core.mobsim.framework.events.MobsimBeforeCleanupEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void notifyMobsimBeforeCleanup(MobsimBeforeCleanupEvent e) {
		if (this.exchanger != null && !this.finished) {
			try {
				exchanger.exchange(SimSignal.KILL);
			} catch (InterruptedException e1) {
				
			}
		}
		this.finished = true;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList();
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.finished = false;
	}
}
