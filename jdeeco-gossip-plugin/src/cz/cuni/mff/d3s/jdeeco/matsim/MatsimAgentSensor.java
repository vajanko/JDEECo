/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.controler.Controler;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimAgentSensor implements AgentSensor {
	
	private Controler controler;
	private Map<Id, ? extends Link> links;
	
	private MatsimAgent agent;
	
	/**
	 * 
	 */
	public MatsimAgentSensor(Controler controler, MatsimAgent agent) {
		this.controler = controler;
		this.agent = agent;
		this.links = this.controler.getNetwork().getLinks();
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.matsim.AgentSensor#getCoord()
	 */
	@Override
	public Position getPosition() {
		Link link = links.get(agent.getCurrentLinkId());
		Coord co = link.getCoord();
		return new Position(co.getX(), co.getY());
	}
}
