/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.sim;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
 */
public interface AgentSensor {
	
	public Integer getNodeId();
	
	public Position getPosition();
	
	public Long getTime();
	
	// TODO: add methods like
	// getSpeed(), setSpeed(), ...
}
