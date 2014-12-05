package cz.cuni.mff.d3s.jdeeco.demo;

import java.util.HashMap;
import java.util.Map;

public class PositionActuator implements PositionAware {
	private final Map<String, Position> positions = new HashMap<String, Position>();
	
	public void updatePosition(String id, Position position) {
		positions.put(id, position);
	}
	
	public Position getPosition(String id) {
		return positions.get(id);
	}
}	
