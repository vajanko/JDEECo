package cz.cuni.mff.d3s.deeco.runners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;

/** 
 * Main entry for launching DEECo simulations.
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public class DEECoSimulation {
	private int nodeCounter = 0xdec0;
	List<DEECoNode> deecoNodes;
	List<DEECoPlugin> instantiatedPlugins;
	List<Class<? extends DEECoPlugin>> nonInstantiatedPlugins;
	SimulationTimer simulationTimer;

	public DEECoSimulation(SimulationTimer simulationTimer) {
		this.simulationTimer = simulationTimer;
		instantiatedPlugins = new ArrayList<>();
		nonInstantiatedPlugins = new ArrayList<>();
		deecoNodes = new ArrayList<>();
	}
	
	public void addPlugin(Class<? extends DEECoPlugin> clazz) {
		nonInstantiatedPlugins.add(clazz);
	}
	
	public void addPlugin(DEECoPlugin nodeWideplugin) {
		instantiatedPlugins.add(nodeWideplugin);		
	}

	public void start(long duration) {
		simulationTimer.start(duration);
	}
	
	public DEECoNode createNode(DEECoPlugin... nodeSpecificPlugins) throws DEECoException, InstantiationException, IllegalAccessException {
		return createNode(nodeCounter++, nodeSpecificPlugins);
	}

	public DEECoNode createNode(int id, DEECoPlugin... nodeSpecificPlugins) throws DEECoException, InstantiationException, IllegalAccessException {
		// Create list of plug-ins for new node
		List<DEECoPlugin> plugins = new LinkedList<DEECoPlugin>();
		plugins.addAll(instantiatedPlugins);
		plugins.addAll(Arrays.asList(nodeSpecificPlugins));
		for (Class<? extends DEECoPlugin> c : nonInstantiatedPlugins) {
			plugins.add(c.newInstance());
		}
		
		DEECoNode node = new DEECoNode(id, simulationTimer, plugins.toArray(new DEECoPlugin[0]));
		deecoNodes.add(node);
		return node;
	}
	
}
