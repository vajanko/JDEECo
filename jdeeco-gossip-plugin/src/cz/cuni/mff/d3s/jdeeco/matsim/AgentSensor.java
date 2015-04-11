/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public interface AgentSensor {
	
	Integer getNodeId();
	
	Position getPosition();
	
	// TODO: add methods like
	// getSpeed(), setSpeed(), ...
}
