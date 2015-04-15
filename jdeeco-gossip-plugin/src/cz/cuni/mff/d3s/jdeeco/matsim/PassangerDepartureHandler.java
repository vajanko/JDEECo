/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.Collection;

import org.matsim.api.core.v01.Id;
import org.matsim.core.api.experimental.events.Event;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.framework.MobsimPassengerAgent;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.interfaces.DepartureHandler;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEngine;
import org.matsim.core.mobsim.qsim.qnetsimengine.QVehicle;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PassangerDepartureHandler implements DepartureHandler {

	private Collection<String> transportModes;
	private QNetsimEngine netsimEngine;
	private QSim simulation;
	
	/**
	 * @param conf 
	 * 
	 */
	public PassangerDepartureHandler(QSim sim, QSimConfigGroup conf, QNetsimEngine qNetsimEngine) {
		this.simulation = sim;
		this.transportModes = conf.getMainMode();
		this.netsimEngine = qNetsimEngine;
	}
	
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.qsim.interfaces.DepartureHandler#handleDeparture(double, org.matsim.core.mobsim.framework.MobsimAgent, org.matsim.api.core.v01.Id)
	 */
	@Override
	public boolean handleDeparture(double now, MobsimAgent agent, Id linkId) {
		if (this.transportModes.contains(agent.getMode())) {
			if (agent instanceof MobsimPassengerAgent) {
				boolean success = handleCarDeparture(now, (MobsimPassengerAgent)agent, linkId);
				//return success;
				if (agent.getId().equals(new IdImpl(3)))
					return success;
			}
		}
		return false ;
	}

	/**
	 * @param now
	 * @param agent
	 * @param linkId
	 */
	private boolean handleCarDeparture(double now, MobsimPassengerAgent agent, Id linkId) {
		Id vehicleId = agent.getPlannedVehicleId();
		if (vehicleId == null)
			return false;
		
		QVehicle vehicle = this.netsimEngine.getVehicles().get(vehicleId);
		if (vehicle == null)
			return false;
		
		if (linkId.equals(vehicle.getCurrentLink()))
			return false;
		
		boolean added = vehicle.addPassenger(agent);
		if (!added)
			return false;
		
		agent.setVehicle(vehicle);
		
		EventsManager eventsManager = simulation.getEventsManager();
		Event event = eventsManager.getFactory().createPersonEntersVehicleEvent(now, agent.getId(), vehicle.getId());
		eventsManager.processEvent(event);
		
		return true;
	}
	
	

}
