/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.basic.v01.IdImpl;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * Provides data about MATSim agent to deeco runtime.
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
 */
public class MatsimAgentSensor implements AgentSensor {
	
	private Id agentId;
	private int nodeId;
	private Network network;
	private MatsimOutputProvider outputs;
	
	/**
	 * Creates a new instance of agent sensor where node ID is considered to be the same as
	 * MATSim agent ID.
	 */
	public MatsimAgentSensor(int nodeId, Network network, MatsimOutputProvider outputs) {
		this(nodeId, String.valueOf(nodeId), network, outputs);
	}
	/**
	 * Creates a new instance of agent sensor with specific MATSim agent ID.
	 */
	public MatsimAgentSensor(int nodeId, String agentId, Network network, MatsimOutputProvider outputs) {
		this.agentId = new IdImpl(agentId);
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
		MatsimOutput out = outputs.getOutput(agentId);
		Link link = network.getLinks().get(out.currentLinkId);
		Coord co = link.getCoord();
		return new Position(co.getX(), co.getY());
	}
}