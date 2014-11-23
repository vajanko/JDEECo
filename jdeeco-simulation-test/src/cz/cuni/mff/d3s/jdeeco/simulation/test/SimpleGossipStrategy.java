package cz.cuni.mff.d3s.jdeeco.simulation.test;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.DirectGossipStrategy;

public final class SimpleGossipStrategy implements DirectGossipStrategy {

	public Collection<String> filterRecipients(Collection<String> recipients) {
		
		return recipients;
	}

}
