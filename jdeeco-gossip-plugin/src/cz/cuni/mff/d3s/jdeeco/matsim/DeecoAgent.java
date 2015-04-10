/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.Arrays;
import java.util.List;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.QSimUtils;
import org.matsim.core.mobsim.qsim.interfaces.MobsimVehicle;
import org.matsim.core.population.routes.NetworkRoute;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class DeecoAgent implements MobsimDriverAgent, DEECoPlugin {

	private Id personId;
	
	private MobsimVehicle vehicle;
	private Id vehicleId;
	
	private State state;
	private String mode;
	private Person person;
	
	private int currentPlanIndex;		// index of currently performing plan activity or leg
	
	private Route currentRoute;
	private int currentRouteIndex;		// index of currently passing link on the route
	
	private Id currentLink;
	private Id nextLink;
	private transient Id destLink;
	
	private Controler controler;
	
	private PlanElement getCurrentPlanElement() {
		return person.getSelectedPlan().getPlanElements().get(currentPlanIndex);
	}
	
	private void continueWithNextPlanElement(double now) {
		currentPlanIndex++;
		
		if (currentPlanIndex >= person.getSelectedPlan().getPlanElements().size())
			return;
		
		PlanElement elem = getCurrentPlanElement();
		if (elem instanceof Activity) {
			this.state = State.ACTIVITY;
			//Activity act = (Activity) elem;
			// TODO: calculate activity end time
		}
		else if (elem instanceof Leg) {
			this.state = State.LEG;
			
			Leg leg = (Leg) elem;
			currentRoute = leg.getRoute();
			if (currentRoute == null) {
				abort(now);
				return;
			}
			
			nextLink = null;
			currentRouteIndex = 0;
			destLink = currentRoute.getEndLinkId();
		}
	}
	
	/**
	 * 
	 */
	public DeecoAgent(Id personId) {
		this.personId = personId;
		this.state = State.ACTIVITY;
		this.mode = TransportMode.car;
		
		this.currentPlanIndex = 0;
	}
	
	public Coord getCurrentCoord() {
		Network net = controler.getNetwork();
		Link link = net.getLinks().get(currentLink);
		if (link == null)
			return null;
		
		return link.getCoord();
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(MatsimPlugin.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		MatsimPlugin matsim = container.getPluginInstance(MatsimPlugin.class);
		
		this.controler = matsim.getControler();
		this.person = controler.getPopulation().getPersons().get(personId);
		Activity act = (Activity)getCurrentPlanElement();
		this.currentLink = act.getLinkId();
		
		MobsimVehicle vehicle = QSimUtils.createDefaultVehicle(new IdImpl(personId + "-vehicle"));
		vehicle.addPassenger(this);
		this.vehicleId = vehicle.getId();
		
		QSim sim = matsim.getSimulation();
		sim.addParkedVehicle(vehicle, currentLink);
		
		// this must be called as last
		sim.insertAgentIntoMobsim(this);
	}
	
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimAgent#getState()
	 */
	@Override
	public State getState() {
		return state;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimAgent#getActivityEndTime()
	 */
	@Override
	public double getActivityEndTime() {
		PlanElement elem = getCurrentPlanElement();
		if (elem instanceof Activity)
			return ((Activity) elem).getEndTime();
		
		return 0;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimAgent#endActivityAndComputeNextState(double)
	 */
	@Override
	public void endActivityAndComputeNextState(double now) {
		continueWithNextPlanElement(now);
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimAgent#endLegAndComputeNextState(double)
	 */
	@Override
	public void endLegAndComputeNextState(double now) {
		continueWithNextPlanElement(now);
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimAgent#abort(double)
	 */
	@Override
	public void abort(double now) {
		state = State.ABORT;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimAgent#getExpectedTravelTime()
	 */
	@Override
	public Double getExpectedTravelTime() {
		PlanElement elem = getCurrentPlanElement();
		if (elem instanceof Leg)
			return ((Leg)elem).getTravelTime();
		
		return null;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimAgent#getMode()
	 */
	@Override
	public String getMode() {
		return mode;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimAgent#notifyArrivalOnLinkByNonNetworkMode(org.matsim.api.core.v01.Id)
	 */
	@Override
	public void notifyArrivalOnLinkByNonNetworkMode(Id linkId) {
		this.currentLink = linkId;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.NetworkAgent#getCurrentLinkId()
	 */
	@Override
	public Id getCurrentLinkId() {
		return currentLink;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.NetworkAgent#getDestinationLinkId()
	 */
	@Override
	public Id getDestinationLinkId() {
		return destLink;
	}
	/* (non-Javadoc)
	 * @see org.matsim.api.core.v01.Identifiable#getId()
	 */
	@Override
	public Id getId() {
		return personId;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.DriverAgent#chooseNextLinkId()
	 */
	@Override
	public Id chooseNextLinkId() {
		if (nextLink != null)
			return nextLink;
		
		if (currentRoute instanceof NetworkRoute) {
			List<Id> ids = ((NetworkRoute)currentRoute).getLinkIds();
			if (currentRouteIndex >= ids.size()) {
				nextLink = destLink;
				return nextLink;
			}
			else {
				nextLink = ids.get(currentRouteIndex);
				return nextLink;
			}
		}

		// means that vehicle is at the end of the route
		return null;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.DriverAgent#notifyMoveOverNode(org.matsim.api.core.v01.Id)
	 */
	@Override
	public void notifyMoveOverNode(Id newLinkId) {
		currentLink = newLinkId;
		currentRouteIndex++;
		nextLink = null;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.VehicleUsingAgent#setVehicle(org.matsim.core.mobsim.qsim.interfaces.MobsimVehicle)
	 */
	@Override
	public void setVehicle(MobsimVehicle veh) {
		vehicle = veh;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.VehicleUsingAgent#getVehicle()
	 */
	@Override
	public MobsimVehicle getVehicle() {
		return vehicle;
	}
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.VehicleUsingAgent#getPlannedVehicleId()
	 */
	@Override
	public Id getPlannedVehicleId() {
		// TODO: here should be decided which car should be used
		
		return vehicleId;
//		PlanElement elem = getCurrentPlanElement();
//		if (elem instanceof Leg) {
//			Route route = ((Leg)elem).getRoute();
//			if (route instanceof NetworkRoute)
//				return ((NetworkRoute)route).getVehicleId();
//		}
//		
//		// assume that agent id is the vehicle id if nothing else is given
//		return this.getId();
	}
}
