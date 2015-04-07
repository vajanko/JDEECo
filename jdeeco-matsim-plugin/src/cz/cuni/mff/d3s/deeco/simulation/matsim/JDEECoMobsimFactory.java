package cz.cuni.mff.d3s.deeco.simulation.matsim;

import java.util.Collection;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.mobsim.framework.MobsimFactory;
import org.matsim.core.mobsim.qsim.ActivityEngine;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.TeleportationEngine;
import org.matsim.core.mobsim.qsim.qnetsimengine.DefaultQSimEngineFactory;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEngine;

/**
 * Mobisim engine factory. It is the configuration point for the MATSim
 * simulation to let it know, which agent source, listeners etc... to use.
 * 
 * @author Michal Kit <kit@d3s.mff.cuni.cz>
 * 
 */
public class JDEECoMobsimFactory implements MobsimFactory {

	private final JDEECoWithinDayMobsimListener listener;
	private final Collection<? extends AdditionAwareAgentSource> agentSources;

	public JDEECoMobsimFactory(JDEECoWithinDayMobsimListener listener,
			Collection<? extends AdditionAwareAgentSource> agentSources) {
		this.listener = listener;
		this.agentSources = agentSources;
	}

	public Mobsim createMobsim(Scenario sc, EventsManager events) {
		QSim qSim = new QSim(sc, events);
		ActivityEngine activityEngine = new ActivityEngine();
		qSim.addMobsimEngine(activityEngine);
		qSim.addActivityHandler(activityEngine);
		QNetsimEngine simEngine = new DefaultQSimEngineFactory()
				.createQSimEngine(qSim);
		qSim.addMobsimEngine(simEngine);
		qSim.addDepartureHandler(simEngine.getDepartureHandler());
		TeleportationEngine teleportationEngine = new TeleportationEngine();
		qSim.addMobsimEngine(teleportationEngine);
		qSim.addQueueSimulationListeners(listener);
		// Add agent sources to the simulation
		for (AdditionAwareAgentSource aaas : agentSources) {
			qSim.addAgentSource(aaas);
			aaas.agentSourceAdded(qSim);
		}
		return qSim;
	}

}
