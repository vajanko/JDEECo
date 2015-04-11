/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class CityGenerator {
	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		
		Scenario sc = ScenarioUtils.createScenario(config);
		
		Network network = sc.getNetwork();
		Population pop = sc.getPopulation();
		PopulationFactory popFactory = pop.getFactory();
		
		Person person = popFactory.createPerson(sc.createId("1"));
		pop.addPerson(person);
		
		Plan plan = popFactory.createPlan();
		person.addPlan(plan);
		
		
		
		MatsimWriter writer = new PopulationWriter(pop, network);
		writer.write("./config/matsim/test.xml");
	}
}
