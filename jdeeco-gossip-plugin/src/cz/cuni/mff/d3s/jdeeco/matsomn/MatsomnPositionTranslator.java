/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsomn;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import cz.cuni.mff.d3s.jdeeco.core.Position;
import cz.cuni.mff.d3s.jdeeco.core.PositionTranslator;

/**
 * Translates MATSim position to OMNeT position
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsomnPositionTranslator implements PositionTranslator {

	private final Position trans;
	
	/**
	 * 
	 */
	public MatsomnPositionTranslator(Network network) {
		// calculate position of the edge nodes
		double minX = 0.0;
		double minY = 0.0;
		
		for (Node node : network.getNodes().values()) {
			Coord c = node.getCoord();
			if (c.getX() < minX)
				minX = c.getX();
			if (c.getY() < minY)
				minY = c.getY();
		}
		
		// Let's give 50m boundary
		minX -= 50.0;
		minY -= 50.0;
		
		trans = new Position(-minX, -minY);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.core.PositionTranslator#translate(cz.cuni.mff.d3s.jdeeco.core.Position)
	 */
	@Override
	public Position translate(Position pos) {
		return new Position(pos.x + trans.x, pos.y + trans.y);
	}

}
