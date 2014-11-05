package cz.cuni.mff.d3s.deeco.simulation.matsim;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.router.util.TravelTime;
import org.matsim.withinday.trafficmonitoring.TravelTimeCollector;
import org.matsim.withinday.trafficmonitoring.TravelTimeCollectorFactory;

import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.simulation.DirectSimulationHost;
import cz.cuni.mff.d3s.deeco.simulation.NetworkKnowledgeDataHandler;
import cz.cuni.mff.d3s.deeco.simulation.Simulation;

public class MATSimSimulation extends Simulation implements MATSimSimulationStepListener {

	private static final String SIMULATION_CALLBACK = "SIMULATION_CALLBACK";

	private long currentMilliseconds;
	private final long simulationStep; // in milliseconds
	private final TravelTime travelTime;
	private final TreeSet<Callback> callbacks;
	private final Map<String, Callback> hostIdToCallback;
	private final Controler controler;
	private final JDEECoWithinDayMobsimListener listener;
	private final MATSimDataProvider matSimProvider;
	private final MATSimDataReceiver matSimReceiver;
	private final Map<String, DirectSimulationHost> hosts;
	private final MATSimExtractor extractor;

	private final NetworkKnowledgeDataHandler knowledgeDataHandler;

	public MATSimSimulation(MATSimDataReceiver matSimReceiver,
			MATSimDataProvider matSimProvider, MATSimUpdater updater, MATSimExtractor extractor, NetworkKnowledgeDataHandler knowledgeDataHandler,
			final Collection<? extends AdditionAwareAgentSource> agentSources,
			String matSimConf) {
		this.knowledgeDataHandler = knowledgeDataHandler;
		this.callbacks = new TreeSet<>();
		this.hostIdToCallback = new HashMap<>();
		this.hosts = new HashMap<>();

		this.controler = new MATSimPreloadingControler(matSimConf);
		this.controler.setOverwriteFiles(true);
		this.controler.getConfig().getQSimConfigGroup()
				.setSimStarttimeInterpretation("onlyUseStarttime");

		double end = this.controler.getConfig().getQSimConfigGroup()
				.getEndTime();
		double start = this.controler.getConfig().getQSimConfigGroup()
				.getStartTime();
		double step = this.controler.getConfig().getQSimConfigGroup()
				.getTimeStepSize();
		Log.i("Starting simulation: matsimStartTime: " + start
				+ " matsimEndTime: " + end);
		this.extractor = extractor;
		this.listener = new JDEECoWithinDayMobsimListener(this, updater, extractor);
		this.matSimProvider = matSimProvider;
		this.matSimReceiver = matSimReceiver;

		Set<String> analyzedModes = new HashSet<String>();
		analyzedModes.add(TransportMode.car);
		travelTime = new TravelTimeCollectorFactory()
				.createTravelTimeCollector(controler.getScenario(),
						analyzedModes);

		controler.addControlerListener(new StartupListener() {
			public void notifyStartup(StartupEvent event) {
				controler.getEvents().addHandler(
						(TravelTimeCollector) travelTime);
				controler.getMobsimListeners().add(
						(TravelTimeCollector) travelTime);
				controler.setMobsimFactory(new JDEECoMobsimFactory(listener,
						agentSources));
			}
		});
		/**
		 * Bind MATSim listener with the agent source. It is necessary to let
		 * the listener know about the jDEECo agents that it needs to update
		 * with data coming from a jDEECo runtime.
		 */
		for (AdditionAwareAgentSource source : agentSources) {
			if (source instanceof JDEECoAgentSource) {
				listener.registerAgentProvider((JDEECoAgentSource) source);
			}
		}

		this.simulationStep = secondsToMilliseconds(step);
		currentMilliseconds = secondsToMilliseconds(controler.getConfig()
				.getQSimConfigGroup().getStartTime());
	}

	public DirectSimulationHost getHost(String id) {
		
		DirectSimulationHost host = hosts.get(id);
		if (host == null) {
			host = new DirectSimulationHost(id, this, knowledgeDataHandler);
			hosts.put(id, host);
		}
		return host;
	}

	public Controler getControler() {
		return this.controler;
	}

	public long getMATSimMilliseconds() {
		return currentMilliseconds;
	}

	public TravelTime getTravelTime() {
		return this.travelTime;
	}

	@Override
	public long getCurrentMilliseconds() {
		return currentMilliseconds;
	}

	@Override
	public synchronized void callAt(long absoluteTime, String hostId) {
		Callback callback = hostIdToCallback.remove(hostId);
		if (callback != null) {
			callbacks.remove(callback);
		}
		callback = new Callback(hostId, absoluteTime);
		hostIdToCallback.put(hostId, callback);
		//System.out.println("For " + absoluteTime);
		callbacks.add(callback);
	}

	@Override
	public void at(double seconds, Mobsim mobsim) {	
		// Exchange data with MATSim
		long milliseconds = secondsToMilliseconds(seconds);
		matSimReceiver.setMATSimData(extractor.extractFromMATSim(listener.getAllJDEECoAgents(), mobsim));
		listener.updateJDEECoAgents(matSimProvider.getMATSimData());
		// Add callback for the MATSim step
		callAt(milliseconds + simulationStep, SIMULATION_CALLBACK);
		DirectSimulationHost host;
		Callback callback;
		// Iterate through all the callbacks until the MATSim callback.
		while (!callbacks.isEmpty()) {
			callback = callbacks.pollFirst();
			if (callback.getHostId().equals(SIMULATION_CALLBACK)) {
				break;
			}
			currentMilliseconds = callback.getAbsoluteTime();
			//System.out.println("At: " + currentMilliseconds);
			host = (DirectSimulationHost) hosts.get(callback.hostId);
			host.at(millisecondsToSeconds(currentMilliseconds));
		}
	}

	public synchronized void run() {
		controler.run();
	}

	private class Callback implements Comparable<Callback> {

		private final long milliseconds;
		private final String hostId;

		public Callback(String hostId, long milliseconds) {
			this.hostId = hostId;
			this.milliseconds = milliseconds;
		}

		public long getAbsoluteTime() {
			return milliseconds;
		}

		public String getHostId() {
			return hostId;
		}

		@Override
		public int compareTo(Callback c) {
			if (c.getAbsoluteTime() < milliseconds) {
				return 1;
			} else if (c.getAbsoluteTime() > milliseconds) {
				return -1;
			} else if (this == c) {
				return 0;
			} else {
				return this.hashCode() < c.hashCode() ? 1 : -1;
			}
		}

		public String toString() {
			return hostId + " " + milliseconds;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((hostId == null) ? 0 : hostId.hashCode());
			result = prime * result
					+ (int) (milliseconds ^ (milliseconds >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Callback other = (Callback) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (hostId == null) {
				if (other.hostId != null)
					return false;
			} else if (!hostId.equals(other.hostId))
				return false;
			if (milliseconds != other.milliseconds)
				return false;
			return true;
		}

		private MATSimSimulation getOuterType() {
			return MATSimSimulation.this;
		}
	}
}
