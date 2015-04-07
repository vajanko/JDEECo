package cz.cuni.mff.d3s.deeco.simulation.matsim;

import java.util.LinkedList;
import java.util.List;

import org.matsim.api.core.v01.Id;

/**
 * Data that is needed by MATSim to execute the simulation.
 * 
 * @author Michal Kit <kit@d3s.mff.cuni.cz>
 * 
 */
public class MATSimInput {
	public List<Id> route = new LinkedList<>();
	public Double speed = Double.POSITIVE_INFINITY;

	public MATSimInput clone() {
		MATSimInput result = new MATSimInput();
		if (route == null) {
			result.route = null;
		} else {
			result.route = new LinkedList<Id>(route);
		}
		result.speed = speed;
		return result;
	}
}
