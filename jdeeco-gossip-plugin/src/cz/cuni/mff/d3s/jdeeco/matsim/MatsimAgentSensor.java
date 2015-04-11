/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimAgentSensor implements AgentSensor {
	
	private int nodeId;
	private Network network;
	private MatsimOutputProvider outputs;
	
	/**
	 * 
	 */
	public MatsimAgentSensor(int nodeId, Network network, MatsimOutputProvider outputs) {
		this.nodeId = nodeId;
		this.outputs = outputs;
		this.network = network;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor#getNodeId()
	 */
	@Override
	public Integer getNodeId() {
		return nodeId;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor#getCoord()
	 */
	@Override
	public Position getPosition() {
		MatsimOutput out = outputs.getOutput(nodeId);
		Link link = network.getLinks().get(out.currentLinkId);
		Coord co = link.getCoord();
		return new Position(co.getX(), co.getY());
	}
}
