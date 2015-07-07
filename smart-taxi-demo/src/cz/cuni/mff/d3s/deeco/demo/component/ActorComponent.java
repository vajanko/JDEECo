/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.core.Position;
import cz.cuni.mff.d3s.jdeeco.sim.AgentSensor;

/**
 * Base class for all components in the demo.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public abstract class ActorComponent {
	public String id;
	public Position position;
	public ActorRole role;
	
	// joined current position and the destination position
	public Integer sector;
	
	// positions of other actors in the system
	public ActorStorage actors;
	
	@Local public Map<String, Long> updates = new HashMap<String, Long>();
	
	@Local public AgentSensor sensor;
	
	public ActorComponent(String id, ActorRole role, AgentSensor sensor) {
		this.id = id;
		this.role = role;
		this.position = new Position();
		this.actors = new ActorStorage();
		this.sensor = sensor;
	}
	
	@Process
	public static void updateGroup(
			@In("sensor") AgentSensor sensor,
			@TriggerOnChange @In("position") Position position,
			@Out("sector") ParamHolder<Integer> sector) {
		
		//Position dest = sensor.getDestination();
		//group.value = String.format("%s-%s", position, dest);
		int y = (int)((53126 - position.y) / 1000) * 10;
		int x = (int)((45275.7 - position.x) / 1000);
		
		sector.value = y + x;
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void updatePosition(
			@In("id") String id,
			@In("sensor") AgentSensor sensor,
			@Out("position") ParamHolder<Position> position) {
		
		position.value = sensor.getPosition();
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void updateActors(
			@In("id") String id,
			@In("sensor") AgentSensor sensor,
			@InOut("actors") ParamHolder<ActorStorage> actors,
			@InOut("updates") ParamHolder<Map<String, Long>> updates) {
		
		long time = sensor.getTime();
		
		Iterator<String> it = actors.value.getActors().iterator();
		while (it.hasNext()) {
			String aid = it.next();
			
			if (updates.value.containsKey(aid)) {
				long lastUpdate = updates.value.get(aid);
				if (time - lastUpdate > 30000) {
					it.remove();
					updates.value.remove(aid);
					continue;
				}
			}
			
			// update time we have seen this agent lastly
			updates.value.put(aid, time);
		}
	}
}
