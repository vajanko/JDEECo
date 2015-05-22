/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import org.matsim.api.core.v01.Id;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.core.Position;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Component
public class SensorComponent {
	
	public String id;
	public Position position;
	public Id currentLink;
	
	@Local
	public AgentSensor sensor;
	
	public SensorComponent(String id, AgentSensor sensor) {
		this.id = id;
		this.sensor = sensor;
	}
	
	@Process
	@PeriodicScheduling(period = 60 * 1000)
	public static void processSensors(
			@In("id") String id,
			@In("sensor") AgentSensor sensor,
			@Out("position") ParamHolder<Position> position) {
		
		position.value = sensor.getPosition();
		
		System.out.println(id + ": " + position.value);
	}
}
