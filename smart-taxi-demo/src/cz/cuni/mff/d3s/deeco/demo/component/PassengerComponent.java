/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.jdeeco.core.Position;
import cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Component
public class PassengerComponent extends ActorComponent {
	
	/**
	 * 
	 */
	public PassengerComponent(String id, AgentSensor sensor) {
		super(id, sensor);
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void process(
			@In("id") String id,
			@In("position") Position position) {
		
		//System.out.println(String.format("%s: %s", id, position));
	}
}
