/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.jdeeco.matsim.MATSimVehicle;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Component
public class DriverComponent extends ActorComponent {
	
	/**
	 * 
	 */
	public DriverComponent(String id, MATSimVehicle vehiclePlugin) {
		super(id, vehiclePlugin);
	}

	@Process
	@PeriodicScheduling(period = 1000)
	private static void process(@In("id") String id) {
		
	}
}
