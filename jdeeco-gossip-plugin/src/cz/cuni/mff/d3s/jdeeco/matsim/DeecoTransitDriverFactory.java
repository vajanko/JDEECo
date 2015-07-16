/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.mobsim.qsim.InternalInterface;
import org.matsim.core.mobsim.qsim.pt.AbstractTransitDriver;
import org.matsim.core.mobsim.qsim.pt.AbstractTransitDriverFactory;
import org.matsim.core.mobsim.qsim.pt.TransitStopAgentTracker;
import org.matsim.pt.Umlauf;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class DeecoTransitDriverFactory implements AbstractTransitDriverFactory {

	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.qsim.pt.AbstractTransitDriverFactory#createTransitDriver(org.matsim.pt.Umlauf, org.matsim.core.mobsim.qsim.pt.TransitStopAgentTracker, org.matsim.core.mobsim.qsim.InternalInterface)
	 */
	@Override
	public AbstractTransitDriver createTransitDriver(Umlauf umlauf,
			TransitStopAgentTracker thisAgentTrackerVehicle,
			InternalInterface internalInterface) {
		
		return new DeecoUmlaufDriver(umlauf, TransportMode.car, thisAgentTrackerVehicle, internalInterface);
	}

}
