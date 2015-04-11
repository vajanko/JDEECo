/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.Arrays;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.QSimUtils;
import org.matsim.core.mobsim.qsim.interfaces.MobsimVehicle;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimAgentPlugin implements DEECoPlugin {

	private int nodeId;
	private Id personId;
	
	private MatsimAgent agent;
	private MatsimAgentSensor agentSensor;
	private Controler controler;

	public MatsimAgentPlugin(Id personId) {
		this.personId = personId;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	public AgentSensor getSensor() {
		return agentSensor;
	}
	public MatsimAgent getAgent() {
		return agent;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(MatsimPlugin.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.nodeId = container.getId();
		
		MatsimPlugin matsim = container.getPluginInstance(MatsimPlugin.class);
		this.controler = matsim.getControler();
		
		Person person = controler.getPopulation().getPersons().get(personId);
		
		this.agent = new MatsimAgent(person);
		this.agentSensor = new MatsimAgentSensor(container.getId(), controler.getNetwork(), matsim.getOutputProvider());
		
		MobsimVehicle vehicle = QSimUtils.createDefaultVehicle(new IdImpl(agent.getPlannedVehicleId().toString()));
		vehicle.addPassenger(agent);
		
		QSim sim = matsim.getSimulation();
		sim.addParkedVehicle(vehicle, agent.getCurrentLinkId());
		
		// this method must be called as last on sim object
		sim.insertAgentIntoMobsim(agent);
		
		// register this plugin to matsim simulation
		matsim.registerPlugin(this);
	}
}
