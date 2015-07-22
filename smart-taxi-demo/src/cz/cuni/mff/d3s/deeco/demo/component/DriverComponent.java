/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;

/**
 * Deeco component class implementing functionality of the driver
 * as described in the smart car sharing demo.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Component
public class DriverComponent extends ActorComponent {
	
	/**
	 * Create a new instance of Deeco driver component with given ID of node where
	 * this component is about to be deployed and a sensor providing node mobility
	 * information.
	 */
	public DriverComponent(int nodeId, AgentSensor sensor) {
		super(AddressHelper.encodeID("D", nodeId), ActorRole.driver, sensor);
	}
}
