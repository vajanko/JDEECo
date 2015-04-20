/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.PlaysRole;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.core.Position;
import cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor;

/**
 * Base class for all components in the demo.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@PlaysRole(PositionAware.class)
public abstract class ActorComponent {
	public String id;
	public Position position;
	
	@Local
	public AgentSensor sensor;
	
	/**
	 * 
	 */
	public ActorComponent(String id, AgentSensor sensor) {
		this.id = id;
		this.position = new Position();
		this.sensor = sensor;
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void updatePosition(
			@In("id") String id,
			@In("sensor") AgentSensor sensor,
			@Out("position") ParamHolder<Position> position) {
		
		position.value = sensor.getPosition();
		
		//System.out.println(id + ": " + position.value.toString());
	}
	
	/**
	 * Updates currently closes station with respect to the current position.
	 */
	/*@Process
	public static void updateCurrentStation(
			@In("application") TaxiApplication application,
			@TriggerOnChange @In("position") Position position,
			@Out("currentStation") ParamHolder<String> currentStation) {
		
		currentStation.value = application.getProximateStation(position);
	}*/
}
