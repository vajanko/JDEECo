/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Component
public class PassengerComponent extends ActorComponent {
	
	/**
	 * 
	 */
	public PassengerComponent(int nodeId, AgentSensor aSensor) {
		super(AddressHelper.encodeID("P", nodeId), ActorRole.passenger, aSensor);
	}
	
//	@Process
//	@PeriodicScheduling(period = 1000)
//	public static void process(
//			@In("id") String id,
//			@In("position") Position position) {
//		
//		//System.out.println(String.format("%s: %s", id, position));
//	}
}
