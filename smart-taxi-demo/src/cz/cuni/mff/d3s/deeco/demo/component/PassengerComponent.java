/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;

/**
 * Deeco component class implementing functionality of the passenger
 * as described in the smart car sharing demo.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
@Component
public class PassengerComponent extends ActorComponent {
	
	/**
	 * Create a new instance of Deeco passenger component with given ID of node where
	 * this component is about to be deployed and a sensor providing node mobility
	 * information.
	 */
	public PassengerComponent(int nodeId, AgentSensor aSensor) {
		super(AddressHelper.encodeID("P", nodeId), ActorRole.passenger, aSensor);
	}
}
