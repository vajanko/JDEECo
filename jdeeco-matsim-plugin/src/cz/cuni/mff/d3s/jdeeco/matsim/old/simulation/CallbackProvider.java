package cz.cuni.mff.d3s.jdeeco.matsim.old.simulation;

public interface CallbackProvider {
	public void callAt(long absoluteTime, String hostId);
}
