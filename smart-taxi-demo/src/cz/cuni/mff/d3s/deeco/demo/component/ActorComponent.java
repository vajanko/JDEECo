/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;

import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.PlaysRole;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.demo.TaxiApplication;
import cz.cuni.mff.d3s.deeco.simulation.matsim.MATSimRouter;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.matsim.MATSimVehicle;
import cz.cuni.mff.d3s.jdeeco.matsim.old.roadtrains.Actuator;
import cz.cuni.mff.d3s.jdeeco.matsim.old.roadtrains.ActuatorType;
import cz.cuni.mff.d3s.jdeeco.matsim.old.roadtrains.Sensor;
import cz.cuni.mff.d3s.jdeeco.matsim.old.roadtrains.SensorType;

/**
 * Base class for all components in the demo.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@PlaysRole(PositionAware.class)
public abstract class ActorComponent {
	public String id;
	public Coord position;
	public Id currentLink;
	
	public String currentStation;
	public Collection<String> passingStations;
	public String nextStation;
	
	@Local
	public Map<String, Position> others = new HashMap<String, Position>();
	
	@Local
	protected TaxiApplication application = new TaxiApplication();
	
	@Local
	public Actuator<List<Id> > routeActuator;
	@Local
	public Actuator<Double> speedActuator;
	@Local
	public Sensor<Id> currentLinkSensor;
	@Local
	public MATSimRouter router;
	
	/**
	 * 
	 */
	public ActorComponent(String id, MATSimVehicle vehiclePlugin) {
		this.id = id;
		this.router = vehiclePlugin.getSimulation().getRouter();
		this.routeActuator = vehiclePlugin.getActuatorProvider().createActuator(ActuatorType.ROUTE);
		this.speedActuator = vehiclePlugin.getActuatorProvider().createActuator(ActuatorType.SPEED);
		this.currentLinkSensor = vehiclePlugin.getSensorProvider().createSensor(SensorType.CURRENT_LINK);
	}
	
	@Process
	@PeriodicScheduling(period = 3600 * 1000)
	public static void processSensors(
			@In("id") String id,
			@In("router") MATSimRouter router,
			@In("currentLinkSensor") Sensor<Id> currentLinkSensor,
			
			@Out("currentLink") ParamHolder<Id> currentLink,
			@Out("position") ParamHolder<Coord> position) {
		
		currentLink.value = currentLinkSensor.read();
		position.value = router.getLink(currentLink.value).getCoord();
		
		System.out.println(id + ": " + position.value.toString());
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
