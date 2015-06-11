/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.sim;

import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class DummySensor implements AgentSensor {

	private int nodeId;
	private Position position;
	private CurrentTimeProvider timer;
	
	/**
	 * 
	 */
	public DummySensor(int nodeId, double x, double y,  CurrentTimeProvider timer) {
		this.nodeId = nodeId;
		this.timer = timer;
		this.position = new Position(x, y);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.sim.AgentSensor#getNodeId()
	 */
	@Override
	public Integer getNodeId() {
		return nodeId;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.sim.AgentSensor#getPosition()
	 */
	@Override
	public Position getPosition() {
		return position;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.sim.AgentSensor#getTime()
	 */
	@Override
	public Long getTime() {
		return this.timer.getCurrentMilliseconds();
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.sim.AgentSensor#getDestination()
	 */
	@Override
	public Position getDestination() {
		return position;
	}

}
