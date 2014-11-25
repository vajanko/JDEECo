package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;

public abstract class IPGossipBaseStrategy implements IPGossipStrategy {

	protected IPController controller;
	protected Collection<String> partitions;

	public IPGossipBaseStrategy(Set<String> partitions, IPController controller) {
		this.partitions = new ArrayList<String>(partitions);
		this.controller = controller;
	}
}
