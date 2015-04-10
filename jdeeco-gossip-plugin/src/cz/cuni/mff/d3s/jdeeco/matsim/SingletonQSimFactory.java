/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.qsim.QSimFactory;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;

/**
 * Extension of {@link QSimFactory} which only creates one instance and then always returns
 * the same one.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SingletonQSimFactory extends QSimFactory {

	private Netsim simulation = null;
	
	/* (non-Javadoc)
	 * @see org.matsim.core.mobsim.framework.MobsimFactory#createMobsim(org.matsim.api.core.v01.Scenario, org.matsim.core.api.experimental.events.EventsManager)
	 */
	@Override
	public Netsim createMobsim(Scenario sc, EventsManager eventsManager) {
		
		if (simulation == null) {
			this.simulation = super.createMobsim(sc, eventsManager);
		}
			
		return simulation;
	}

}
