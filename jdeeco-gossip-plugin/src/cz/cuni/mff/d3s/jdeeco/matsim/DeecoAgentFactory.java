/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;

/**
 * Creates extended agents for standard QSim simulation which are connected to deeco component.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class DeecoAgentFactory implements AgentFactory {

	private Netsim simulation;
	
	/**
	 * 
	 */
	public DeecoAgentFactory(Netsim simulation) {
		this.simulation = simulation;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.qsim.agents.AgentFactory#createMobsimAgentFromPerson(org.matsim.api.core.v01.population.Person)
	 */
	@Override
	public MobsimAgent createMobsimAgentFromPerson(Person p) {
		return new MatsimAgent(p, simulation);
	}

}
