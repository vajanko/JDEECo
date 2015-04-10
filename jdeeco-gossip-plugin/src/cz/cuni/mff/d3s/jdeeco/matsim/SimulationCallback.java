/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;

/**
 * Almost completely taken from: cz.cuni.mff.d3s.deeco.simulation.matsim.MATSimSimulation.Callback
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SimulationCallback implements Comparable<SimulationCallback> {
	private final long milliseconds;
	private final int hostId;
	private final TimerEventListener listener;

	public SimulationCallback(int hostId, long milliseconds, TimerEventListener listener) {
		this.hostId = hostId;
		this.milliseconds = milliseconds;
		this.listener = listener;
	}

	public long getAbsoluteTime() {
		return milliseconds;
	}
	public void execute(long time) {
		listener.at(time);
	}

	@Override
	public int compareTo(SimulationCallback c) {
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
		result = prime * result + hostId;
		result = prime * result + (int) (milliseconds ^ (milliseconds >>> 32));
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
		SimulationCallback other = (SimulationCallback) obj;
		if (hostId != other.hostId)
			return false;
		if (milliseconds != other.milliseconds)
			return false;
		return true;
	}
}
