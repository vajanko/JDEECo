package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;

/**
 * Base class for {@link IPGossipStrategy} implementations using ensemble partitioning.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public abstract class IPGossipPartitionBaseStrategy implements IPGossipStrategy {

	protected IPController controller;
	protected Collection<String> partitions;

	public IPGossipPartitionBaseStrategy(Set<String> partitions, IPController controller) {
		this.partitions = new ArrayList<String>(partitions);
		this.controller = controller;
	}
}
