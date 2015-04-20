/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim.transit;

import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.mobsim.qsim.pt.TransitVehicle;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import cz.cuni.mff.d3s.jdeeco.matsim.DeecoAgent;
import cz.cuni.mff.d3s.jdeeco.matsim.MatsimTransitAgent;


/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PassengerAgent extends MatsimTransitAgent implements DeecoAgent {
	
	public static PassengerAgent createAgent(Person p, Netsim simulation) {
		PassengerAgent agent = new PassengerAgent(p, simulation);
		return agent;
	}
	
	protected PassengerAgent(final Person p, final Netsim simulation) {
		super(p, simulation);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.matsim.MatsimTransitAgent#getDesiredAccessStopId()
	 */
	@Override
	public Id getDesiredAccessStopId() {
		// stop from which the agent wants to depart
		return super.getDesiredAccessStopId();
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.matsim.MatsimTransitAgent#getDesiredDestinationStopId()
	 */
	@Override
	public Id getDesiredDestinationStopId() {
		// stop where the agent wants to leave the car
		return super.getDesiredDestinationStopId();
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.matsim.MatsimTransitAgent#getEnterTransitRoute(org.matsim.pt.transitSchedule.api.TransitLine, org.matsim.pt.transitSchedule.api.TransitRoute, java.util.List, org.matsim.core.mobsim.qsim.pt.TransitVehicle)
	 */
	@Override
	public boolean getEnterTransitRoute(TransitLine line, TransitRoute transitRoute, List<TransitRouteStop> stopsToCome, TransitVehicle transitVehicle) {
		// TODO Auto-generated method stub
		return super.getEnterTransitRoute(line, transitRoute, stopsToCome, transitVehicle);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.matsim.MatsimTransitAgent#getExitAtStop(org.matsim.pt.transitSchedule.api.TransitStopFacility)
	 */
	@Override
	public boolean getExitAtStop(TransitStopFacility stop) {
		// TODO Auto-generated method stub
		return super.getExitAtStop(stop);
	}
	
}
