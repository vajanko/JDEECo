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
import org.matsim.core.mobsim.framework.events.MobsimInitializedEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimInitializedListener;
import org.matsim.core.mobsim.qsim.QSim;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;

/**
 * Plugin enabling MATSim simulation. Use only one instance of this plugin. 
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
 */
public class MatsimPlugin implements DEECoPlugin, TimerEventListener, MobsimInitializedListener {
	public static final String MATSIM_CONFIG = "deeco.matsim.config";
	/**
	 * Default path to MATSim configuration file
	 */
	public static final String MATSIM_CONFIG_DEFAULT = "config/matsim/config.xml";

	private Controler controler;
	private QSim simulation;
	private MatsimTimer timer;
	
	private Exchanger<Object> exchanger;
	private MatsimOutputProvider outputs = new MatsimOutputProvider();
	//private Map<Integer, MatsimAgentPlugin> agentPlugins = new HashMap<Integer, MatsimAgentPlugin>();
	
	private Collection<MatsimAgent> agents = new ArrayList<MatsimAgent>();
	
	public MatsimPlugin() {
		// create controller
		String configFile = System.getProperty(MATSIM_CONFIG, MATSIM_CONFIG_DEFAULT);
		this.controler = new PreloadingControler(configFile);
		this.controler.setOverwriteFiles(true);
		
		SingletonQSimFactory mobsimFactory = new SingletonQSimFactory();
		this.controler.addMobsimFactory("qsim", mobsimFactory);
		
		this.controler.getMobsimListeners().add(this);
		
		this.simulation = (QSim)mobsimFactory.createMobsim(controler.getScenario(), controler.getEvents());
		
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
		
		for (MatsimAgent agent : this.agents) {
			MatsimOutput out = new MatsimOutput(agent.getCurrentLinkId(), agent.getState());
			outputs.put(agent.getId(), out);
		}
		
		return outputs;
	}
	
	public Controler getControler() {
		return controler;
	}
	public QSim getSimulation() {
		return simulation;
	}
	public SimulationTimer getTimer() {
		return timer;
	}
	public MatsimOutputProvider getOutputProvider() {
		return outputs;
	}
	public AgentSensor createAgentSensor(int nodeId) {
		MatsimAgentSensor sensor = new MatsimAgentSensor(nodeId, controler.getNetwork(), outputs);
		return sensor;
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
		
		// there are two modes of matsim simulation:
		// 1) single matsim simulation - only one thread, matsim updates its outputs itself
		// 2) multiple threads (together with oment) - matsim exchanges its outputs with the other thread
		if (exchanger != null) {
			try {
				/*Object inputs =*/ exchanger.exchange(getOutputs());
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
		
		for (MobsimAgent ma : this.simulation.getAgents()) {
			MatsimAgent agent = (MatsimAgent)ma;
			if (agent != null) {
				this.agents.add(agent);
			}
		}
		
		// update with initial values
		this.outputs.updateOutputs(getOutputs());
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
		// nothing to initialise
	}
}
