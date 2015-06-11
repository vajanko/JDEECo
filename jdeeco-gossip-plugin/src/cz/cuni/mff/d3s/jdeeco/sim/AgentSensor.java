/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.sim;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public interface AgentSensor {
	
	public Integer getNodeId();
	
	public Position getPosition();
	
	public Position getDestination();
	
	public Long getTime();
	
	// TODO: add methods like
	// getSpeed(), setSpeed(), ...
}
