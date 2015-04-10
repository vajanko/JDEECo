/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.omnet;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNeTNative;
import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;
import cz.cuni.mff.d3s.jdeeco.core.Position;
import cz.cuni.mff.d3s.jdeeco.core.PositionTranslator;
import cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimAgentPlugin;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimPlugin;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimOmnetAdapter implements DEECoPlugin, TimerEventListener {

	private int nodeId;
	private AgentSensor sensor;
	private PositionTranslator translator;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		// even those OMNeTSimulation class is not necessary directly this plugin is
		// useless without omnet
		return Arrays.asList(MatsimPlugin.class, MatsimAgentPlugin.class, OMNeTSimulation.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		MatsimPlugin matsim = container.getPluginInstance(MatsimPlugin.class);
		matsim.register(this);
		
		MatsimAgentPlugin agent = container.getPluginInstance(MatsimAgentPlugin.class);
		this.sensor = agent.getAgentSensor();
		
		this.translator = new MatsimOmnetPositionTranslator(matsim.getControler().getNetwork());

		this.nodeId = container.getId();		
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.timer.TimerEventListener#at(long)
	 */
	@Override
	public void at(long time) {
		// translate manet to omnet
		Position pos = translator.translate(sensor.getPosition());
		// modify omnet node
		OMNeTNative.nativeSetPosition(nodeId, pos.x, pos.y, 0);
	}
}
