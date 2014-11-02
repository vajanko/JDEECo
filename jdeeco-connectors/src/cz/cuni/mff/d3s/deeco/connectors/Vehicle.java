package cz.cuni.mff.d3s.deeco.connectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Component
public class Vehicle extends EnsembleComponent {
	
	static final Double MIN_DISTANCE = 2d;
	static final Double MAX_DISTANCE = 4d;
	static final Double MAX_VELOCITY = 5d;
	// coordinators outside this distance will be removed from member's list of coordinators
	static final Double DEAD_DISTANCE = 50d;
	
	public String dest;
	public Double velocity;
	public Position position;
	public Map<String, Position> leaders;

	public Vehicle(String id, String dest) {
		this(id, dest, 0d, 0d);
	}
	public Vehicle(String id, String dest, Double xPos, Double yPos) {
		super(id);
		this.dest = dest;
		this.velocity = 0d;
		this.position = new Position(xPos, yPos);
		this.leaders = new HashMap<String, Position>();
	}
	
	private static Double closestLeaderDist(Position pos, Collection<Position> positions) {
	
		Double minDist = Double.MAX_VALUE;
		//Position minPos = null;
		
		Iterator<Position> it = positions.iterator();
		while (it.hasNext()) {
			Double dist = pos.distance(it.next());
			if (dist < minDist) {
				minDist = dist;
				//minPos = it.next();
			}
			
			it.remove();
		}
		
		return minDist;
	}
	
	private static Position calculateCurrentPosition(Position pos, Double velocity) {
		pos.x += velocity;
		pos.y += 0;
		
		return pos;
	}
	
	private static void removeDeadLeaders(Position current, Map<String, Position> leaders) {
	
		Object[] keys = leaders.keySet().toArray();
		
		for (Object key : keys) {
			if (current.distance(leaders.get(key)) >= DEAD_DISTANCE)
				leaders.remove(key);
		}
	}
	
	@Process
	@PeriodicScheduling(period = 2000)
	public static void process(
			@In("id") String id, 
			@In("dest") String dest,
			@InOut("leaders") ParamHolder<Map<String, Position>> leaders,
			@InOut("velocity") ParamHolder<Double> velocity,
			@InOut("position") ParamHolder<Position> position
			) {
		
		String message = id + ": ";
		
		// remove leaders that are too distant
		removeDeadLeaders(position.value, leaders.value);
		
		if (leaders.value.size() == 0) {
			message += "is a leader ";
		}
		else {
			message += "is a follower of:";
			for (String key : leaders.value.keySet())
				message += " " + key;
		}
		
		// find out current position
		position.value = calculateCurrentPosition(position.value, velocity.value);
		message += ", position: " + position.value;
		
		if (leaders.value.size() == 0) {
			// leader
			if (velocity.value < MAX_VELOCITY)
				velocity.value += 1;
		}
		else {
			// follower
			Double minDist = closestLeaderDist(position.value, leaders.value.values());
			if (minDist < MIN_DISTANCE) {
				// slow down
				velocity.value -= 1;
			}
			else {
				// speed up
				if (velocity.value < MAX_VELOCITY)
					velocity.value += 1;
			}
			message += "(min dist. " + minDist + ")";
		}
		
		message += ", velocity: " + velocity.value;
		System.out.println(message);
	}
}
