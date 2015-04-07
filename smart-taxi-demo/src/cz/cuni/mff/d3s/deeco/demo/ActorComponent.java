/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * Base class for all components in the demo.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public abstract class ActorComponent {
	public String id;
	public Position position;
	
	public String currentStation;
	public Collection<String> passingStations;
	public String nextStation;
	
	@Local
	public Map<String, Position> others = new HashMap<String, Position>();
	
	@Local
	protected TaxiApplication application = new TaxiApplication();
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void measurePosition(
			@In("application") TaxiApplication application,
			@Out("position") ParamHolder<Position> position) {
		
		position.value = application.getCurrentPosition();
	}
	
	/**
	 * Updates currently closes station with respect to the current position.
	 */
	@Process
	public static void updateCurrentStation(
			@In("application") TaxiApplication application,
			@TriggerOnChange @In("position") Position position,
			@Out("currentStation") ParamHolder<String> currentStation) {
		
		currentStation.value = application.getProximateStation(position);
	}
}
