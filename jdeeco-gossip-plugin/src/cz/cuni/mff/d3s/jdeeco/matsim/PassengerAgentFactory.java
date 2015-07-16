/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.mobsim.qsim.pt.MobsimDriverPassengerAgent;

/**
 * Factory class for creating transit vehicle passenger agents.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PassengerAgentFactory implements AgentFactory {
	private final Netsim simulation;


	public PassengerAgentFactory(final Netsim simulation) {
		this.simulation = simulation;
	}

	@Override
	public MobsimDriverPassengerAgent createMobsimAgentFromPerson(final Person p) {
		PassengerAgent agent = PassengerAgent.createAgent(p, this.simulation);
		return agent;
	}
}
