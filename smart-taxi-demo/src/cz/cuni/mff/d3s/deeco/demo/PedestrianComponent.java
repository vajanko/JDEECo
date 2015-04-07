/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Component
public class PedestrianComponent extends ActorComponent {
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void process(@In("id") String id) {
		
	}
	
	
}
