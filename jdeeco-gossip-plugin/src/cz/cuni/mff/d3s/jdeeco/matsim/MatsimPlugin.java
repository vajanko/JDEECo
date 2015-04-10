/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.Arrays;
import java.util.List;

import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.qsim.QSim;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;

/**
 * Plugin enabling MATSim simulation. Use only one instance of this plugin. 
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimPlugin implements DEECoPlugin {

	private Controler controler;
	private QSim simulation;
	private MatsimSimulationTimer timer;
	
	public MatsimPlugin(String configFile) {
		// create controller
		this.controler = new PreloadingControler(configFile);
		this.controler.setOverwriteFiles(true);
		
		SingletonQSimFactory mobsimFactory = new SingletonQSimFactory();
		this.controler.addMobsimFactory("qsim", mobsimFactory);
		
		this.simulation = (QSim)mobsimFactory.createMobsim(controler.getScenario(), controler.getEvents());
		
		this.timer = new MatsimSimulationTimer(controler);
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
	/**
	 * Registers timer listener which will be called at each simulation step
	 * 
	 * @param listener Instance of listener to be registered.
	 */
	public void register(TimerEventListener listener) {
		this.timer.register(listener);
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
		
	}
}
